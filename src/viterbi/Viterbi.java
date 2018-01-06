package viterbi;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import static com.google.common.collect.Maps.immutableEnumMap;


public class Viterbi {

	public static int [] viterbi(int numStates, int numObservations,
			double [] initialDistrib,
			double [][] transitionProbs, double [][] emissionProbs,
			int [] observations) {
		if (numStates < 1) {
			throw new IllegalArgumentException("numStates = " + numStates);
		}
		if (numObservations < 1) {
			throw new IllegalArgumentException("numObservations = " + numStates);
		}
		if (initialDistrib.length != numStates) {
			throw new IllegalArgumentException("initialDistrib.length = " + initialDistrib.length);
		}
		if (transitionProbs.length != numStates) {
			throw new IllegalArgumentException("transitionProbs.length = " + transitionProbs.length);
		}
		for (double [] row : transitionProbs) {
			if (row.length != numStates) {
				throw new IllegalArgumentException("row.length = " + row.length);
			}
		}
		if (emissionProbs.length != numStates) {
			throw new IllegalArgumentException("transitionProbs.length = " + transitionProbs.length);
		}
		for (double [] row : emissionProbs) {
			if (row.length != numObservations) {
				throw new IllegalArgumentException("row.length = " + row.length);
			}
		}
		if (observations.length < 1) {
			throw new IllegalArgumentException("observations.length = " + observations.length);
		}
		for (int observation: observations) {
			if ((observation < 0) || (observation >= numObservations)) {
				throw new IllegalArgumentException("observation = " + observation);
			}
		}

		// ------------------------------------------------------------
		double [][] stateProbsForObservations = new double[numStates][];
		int [][] prevStates = new int[numStates][];
		for (int i = 0; i < numStates; ++i) {
			stateProbsForObservations[i] = new double[observations.length];
			prevStates[i] = new int[observations.length];
			
			stateProbsForObservations[i][0] = initialDistrib[i] * emissionProbs[i][observations[0]];
			prevStates[i][0] = -1;
		}
		
		for (int i = 1; i < observations.length; ++i) {
			for (int j = 0; j < numStates; ++j) {
				double maxProb = 0.0;
				int prevStateWithMaxProb = -1;
				for (int k = 0; k < numStates; ++k) {
					double prob = stateProbsForObservations[k][i - 1] * transitionProbs[k][j];
					if (prob > maxProb) {
						maxProb = prob;
						prevStateWithMaxProb = k;
					}
				}
				stateProbsForObservations[j][i] = maxProb * emissionProbs[j][observations[i]];
				prevStates[j][i] =  prevStateWithMaxProb;
			}
		}
		
		
		for (int i = 0; i < numStates; ++i) {
			for (int j = 0; j < observations.length; ++j) {
				System.out.println("state #" + i + " observation #" + j + " --> " + stateProbsForObservations[i][j] + " " + prevStates[i][j]);
			}
		}
		
		// ------------------------------------------------------------
		int stateWithMaxProb = 0;
		double maxProb = stateProbsForObservations[stateWithMaxProb][observations.length - 1];
		for (int i = 1; i < numStates; ++i) {
			double prob = stateProbsForObservations[i][observations.length - 1];
			if (prob > maxProb) {
				maxProb = prob;
				stateWithMaxProb = i;
			}
		}
		
		int [] result = new int[observations.length];
		
		for (int i = observations.length - 1; i >= 0; --i) {
			result[i] = stateWithMaxProb;
			stateWithMaxProb = prevStates[stateWithMaxProb][i];
		}
		
		
		return result;
	}


	public static class Key<T> {
		public T key1, key2;

		public Key(T key1, T key2) {
			this.key1 = key1;
			this.key2 = key2;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key))
				return false;
			Key<T> ref = (Key<T>) obj;
			return this.key1.equals(ref.key1) && this.key2.equals(ref.key2);
		}

		@Override
		public int hashCode() {
			return key1.hashCode() ^ key2.hashCode();
		}
	}
	

	public static List<String> viterbiWrapper(Set<String> states,
										Set<String> emissions,
			Map<Key<String>, Double> transitionProbs,
			Map<Key<String>, Double> emissionProbs,
			Map<String, Double> initProbs,
			List<String> observations) {
		Map<String, Integer> stateIdxs = new HashMap<>();
		Map<Integer, String> revStateIdxs = new HashMap<>();
		Map<String, Integer> emissionIdxs = new HashMap<>();
		
		double [] initialDistrib = new double[states.size()];
		double [][] transProbs = new double[states.size()][states.size()];
		double [][] emitProbs = new double[states.size()][emissions.size()];
		int [] obses = new int[observations.size()];
		
		for (String state: states) {
			stateIdxs.put(state, stateIdxs.size());
			revStateIdxs.put(stateIdxs.get(state), state);
		}
		for (String emission: emissions) {
			emissionIdxs.put(emission, emissionIdxs.size());
		}

		
		for (String state: states) {
			int idx1 = stateIdxs.get(state);
			initialDistrib[idx1] = initProbs.getOrDefault(state, 0.0);
		}
		
		for (String state: states) {
			for (String state2: states) {
				double transitionProb = transitionProbs.getOrDefault(new Key<String>(state, state2), 0.0);
				int idx1 = stateIdxs.get(state);
				int idx2 = stateIdxs.get(state2);
				transProbs[idx1][idx2] = transitionProb;
			}
		}
		

		for (String state: states) {
			for (String emission: emissions) {
				double emissionProb = emissionProbs.getOrDefault(new Key<String>(state, emission), 0.0);
				int idx1 = stateIdxs.get(state);
				int idx2 = emissionIdxs.get(emission);
				emitProbs[idx1][idx2] = emissionProb;
			}
		}

		for (int i = 0; i < observations.size(); ++i) {
			obses[i] = emissionIdxs.get(observations.get(i));
		}
		
		int [] result = viterbi(states.size(), emissions.size(),
				initialDistrib, transProbs, emitProbs, obses);
		List<String> ret = new ArrayList<String>(result.length);
		
		for (int r: result) {
			ret.add(revStateIdxs.get(r));
		}
		
		return ret;
	}
	
	
	public static class ViterbiModel<S extends Enum<S>, T extends Enum<T>> {
		public final ImmutableMap<S, Double> initialDistributions;
		public final ImmutableTable<S, S, Double> transitionProbabilities;
		public final ImmutableTable<S, T, Double> emissionProbabilities;
		
		private ViterbiModel(ImmutableMap<S, Double> initialDistributions, 
				ImmutableTable<S, S, Double> transitionProbabilities,
				ImmutableTable<S, T, Double> emissionProbabilities) {
			this.initialDistributions = checkNotNull(initialDistributions);
			this.transitionProbabilities = checkNotNull(transitionProbabilities);
			this.emissionProbabilities = checkNotNull(emissionProbabilities);
		}
		
		public static <S extends Enum<S>, T extends Enum<T>> Builder<S, T> builder() {
			return new Builder<>();
		}
		
		public static class Builder<S extends Enum<S>, T extends Enum<T>> {
			private ImmutableMap<S, Double> initialDistributions;
			private ImmutableTable.Builder<S, S, Double> transitionProbabilities = ImmutableTable.builder();
			private ImmutableTable.Builder<S, T, Double> emissionProbabilities = ImmutableTable.builder();
			
			public ViterbiModel<S, T> build() {
				return new ViterbiModel<S, T>(immutableEnumMap(initialDistributions), transitionProbabilities.build(), emissionProbabilities.build());
			}
			
			public Builder<S, T> withInitialDistributions(ImmutableMap<S, Double> initialDistributions) {
				this.initialDistributions = initialDistributions;
				return this;
			}
			
			public Builder<S, T> withTransitionProbability(S src, S dest, Double prob) {
				transitionProbabilities.put(src, dest, prob);
				return this;
			}

			public Builder<S, T> withEmissionProbability(S state, T emission, Double prob) {
				emissionProbabilities.put(state, emission, prob);
				return this;
			}
		}
	}
	
	public static class ViterbiMachine<S extends Enum<S>, T extends Enum<T>> {
		private final List<S> possibleStates;
		private final List<T> possibleObservations;

		private final ViterbiModel<S, T> model;
		private final ImmutableList<T> observations;
		
		private Table<S, Integer, Double> stateProbsForObservations = HashBasedTable.create();
		private Table<S, Integer, Optional<S>> previousStatesForObservations = HashBasedTable.create();
		
		private int step;
		
		public ViterbiMachine(ViterbiModel<S, T> model, ImmutableList<T> observations) {
			this.model = checkNotNull(model);
			this.observations = checkNotNull(observations);
			
			possibleStates = ImmutableList.copyOf(getPossibleStates());
			possibleObservations = ImmutableList.copyOf(getPossibleObservations());
			
			validate();
			initialize();
		}

		private void validate() {
			if (model.initialDistributions.size() != possibleStates.size()) {
				throw new IllegalArgumentException("model.initialDistributions.size() = " + model.initialDistributions.size());
			}
			double sumInitProbs = 0.0;
			for (double prob: model.initialDistributions.values()) {
				sumInitProbs += prob;
			}
			if (!doublesEqual(sumInitProbs, 1.0)) {
				throw new IllegalArgumentException("sumInitProbs = " + sumInitProbs);
			}
			if (observations.size() < 1) {
				throw new IllegalArgumentException("observations.size() = " + observations.size());
			}
			if (model.transitionProbabilities.size() < 1) {
				throw new IllegalArgumentException("model.transitionProbabilities.size() = " + model.transitionProbabilities.size());
			}
			for (S row : model.transitionProbabilities.rowKeySet()) {
				double sumRowProbs = 0.0;
				for (double prob : model.transitionProbabilities.row(row).values()) {
					sumRowProbs += prob;
				}
				if (!doublesEqual(sumRowProbs, 1.0)) {
					throw new IllegalArgumentException("'" + row + "' sumRowProbs = " + sumRowProbs);
				}
			}
			if (model.emissionProbabilities.size() < 1) {
				throw new IllegalArgumentException("model.emissionProbabilities.size() = " + model.emissionProbabilities.size());
			}
			for (S row : model.emissionProbabilities.rowKeySet()) {
				double sumRowProbs = 0.0;
				for (double prob : model.emissionProbabilities.row(row).values()) {
					sumRowProbs += prob;
				}
				if (!doublesEqual(sumRowProbs, 1.0)) {
					throw new IllegalArgumentException("'" + row + "' sumRowProbs = " + sumRowProbs);
				}
			}
		}
		
		private static <S, T, V> V getOrDefault(Table<S, T, V> table, S key1, T key2, V defaultValue) {
			V ret = table.get(key1, key2);
			if (ret == null) {
				ret = defaultValue;
			}
			return ret;
		}
		
		private void initialize() {
			final T firstObservation = observations.get(0);
			for (S state : possibleStates) {
				stateProbsForObservations.put(state, 0, model.initialDistributions.getOrDefault(state, 0.0) * getOrDefault(model.emissionProbabilities, state, firstObservation, 0.0));
				previousStatesForObservations.put(state, 0, Optional.<S>empty());
			}
			
			step = 1;
		}
		
		public void nextStep() {
			if (step >= observations.size()) {
				throw new IllegalStateException("already finished last step");
			}

			for (S state : possibleStates) {
				double maxProb = 0.0;
				Optional<S> prevStateWithMaxProb = Optional.empty();
				for (S state2 : possibleStates) {
					double prob = getOrDefault(stateProbsForObservations, state2, step - 1, 0.0) * getOrDefault(model.transitionProbabilities, state2, state, 0.0);
					if (prob > maxProb) {
						maxProb = prob;
						prevStateWithMaxProb = Optional.of(state2);
					}
				}
				stateProbsForObservations.put(state, step, maxProb * getOrDefault(model.emissionProbabilities, state, observations.get(step), 0.0));
				previousStatesForObservations.put(state, step, prevStateWithMaxProb);
			}
			
			++step;
		}
		
		public ImmutableTable<S, Integer, Double> getProbabilitiesForObservations() {
			return ImmutableTable.copyOf(stateProbsForObservations);
		}

		public ImmutableTable<S, Integer, Optional<S>> getPreviousStatesObservations() {
			return ImmutableTable.copyOf(previousStatesForObservations);
		}
		
		public List<S> finish() {
			if (step != observations.size()) {
				throw new IllegalStateException("step = " + step);
			}
			
			S stateWithMaxProb = possibleStates.get(0);
			double maxProb = stateProbsForObservations.get(stateWithMaxProb, observations.size() - 1);
			for (S state : possibleStates) {
				double prob = stateProbsForObservations.get(state, observations.size() - 1);
				if (prob > maxProb) {
					maxProb = prob;
					stateWithMaxProb = state;
				}
			}
			
			List<S> result = new ArrayList<>();
			
			for (int i = observations.size() - 1; i >= 0; --i) {
				result.add(stateWithMaxProb);
				stateWithMaxProb = previousStatesForObservations.get(stateWithMaxProb, i).orElse(null);
			}
			
			return Lists.reverse(result);
		}

		public List<S> calculate() {
			for (int i = 0; i < observations.size() - 1; ++i) {
				nextStep();
			}
			return finish();
		}
		
		private S[] getPossibleStates() {
			return getEnumsFromIterator(model.initialDistributions.keySet().iterator());
		}

		private T[] getPossibleObservations() {
			return getEnumsFromIterator(observations.iterator());
		}

		private static <X extends Enum<X>> X[] getEnumsFromIterator(Iterator<X> it) {
			if (!it.hasNext()) {
				throw new IllegalStateException("iterator should have at least one element");
			}
			Enum<X> val1 = it.next();
			return val1.getDeclaringClass().getEnumConstants();
		}
		
		private static boolean doublesEqual(double d1, double d2) {
			return Math.abs(d1 - d2) < 0.0000001;
		}
	}
	
	
	enum States {
		HEALTHY, FEVER
	};

	enum Observations {
		OK, COLD, DIZZY
	};
	
	
	public static void main(String[] args) {
		ViterbiModel<States, Observations> model = ViterbiModel.<States, Observations>builder()
				.withInitialDistributions(ImmutableMap.<States, Double>builder()
						.put(States.HEALTHY, 0.6)
						.put(States.FEVER, 0.4)
						.build())
				.withTransitionProbability(States.HEALTHY, States.HEALTHY, 0.7)
				.withTransitionProbability(States.HEALTHY, States.FEVER, 0.3)
				.withTransitionProbability(States.FEVER, States.HEALTHY, 0.4)
				.withTransitionProbability(States.FEVER, States.FEVER, 0.6)
				.withEmissionProbability(States.HEALTHY, Observations.OK, 0.5)
				.withEmissionProbability(States.HEALTHY, Observations.COLD, 0.4)
				.withEmissionProbability(States.HEALTHY, Observations.DIZZY, 0.1)
				.withEmissionProbability(States.FEVER, Observations.OK, 0.1)
				.withEmissionProbability(States.FEVER, Observations.COLD, 0.3)
				.withEmissionProbability(States.FEVER, Observations.DIZZY, 0.6)
				.build();
		
		ImmutableList<Observations> observations = ImmutableList.of(Observations.OK, Observations.COLD, Observations.DIZZY);
		
		ViterbiMachine<States, Observations> machine = new ViterbiMachine<>(model, observations);
		System.out.println(machine.calculate());
		
/*
		double [] initialDistrib = {0.6, 0.4};
		double [][] transitionProbs = {{0.7, 0.3}, {0.4, 0.6}};
		double [][] emissionProbs = {{0.5, 0.4, 0.1}, {0.1, 0.3, 0.6}};
		int [] observations = {0, 1, 2};

		int [] states = viterbi(2, 3, initialDistrib, transitionProbs, emissionProbs, observations);
*/
/*		
//                                       P    V    N    D
		double [] initialDistrib = 
                                       {1.0, 0.0, 0.0, 0.0};
		double [][] transitionProbs = 
                                      {{0.0, 1.0, 0.0, 0.0},     // P
                                       {0.0, 0.0, 0.0, 1.0},     // V
                                       {0.0, 0.0, 0.0, 0.0},     // N
                                       {0.0, 0.0, 1.0, 0.0},     // D
                                      };
		double [][] emissionProbs = {// Je0  Mange1  Tue2  Pomme3  Mouche4  Une5
                                     {  1.0,  0.0,   0.0,   0.0,    0.0,    0.0},    // P
                                     {  0.0,  0.5,   0.5,   0.0,    0.0,    0.0},    // V
                                     {  0.0,  0.0,   0.0,   0.5,    0.5,    0.0},    // N
                                     {  0.0,  0.0,   0.0,   0.0,    0.0,    1.0},    // D
                                    };
		int [] observations = {0, 1, 5, 3};


		
		int [] states = viterbi(4, 6, initialDistrib, transitionProbs, emissionProbs, observations);
		
		for (int state: states) {
			System.out.println(state);
		}

*/
/*
		List<String> states = viterbiWrapper(
		new HashSet<String>(){{
			add("P"); add("V"); add("N"); add("D");
		}},
		new HashSet<String>(){{
			add("Je"); add("Te"); add("Ma"); add("Manage");
			add("Tue"); add("Montre"); add("Pomme"); add("Mouche");
			add("Une");
		}},
		new HashMap<Key<String>, Double>() {{
			put(new Key<String>("P", "V"), 0.3333333);
			put(new Key<String>("P", "P"), 0.3333333);
			put(new Key<String>("P", "N"), 0.3333334);
			put(new Key<String>("V", "D"), 0.5);
			put(new Key<String>("V", "P"), 0.5);
			put(new Key<String>("D", "N"), 1.0);
		}},
		new HashMap<Key<String>, Double>() {{
			put(new Key<String>("P", "Je"),     0.3333333);
			put(new Key<String>("P", "Te"),     0.3333333);
			put(new Key<String>("P", "Ma"),     0.3333334);
			put(new Key<String>("V", "Mange"),  0.3333333);
			put(new Key<String>("V", "Tue"),    0.3333333);
			put(new Key<String>("V", "Montre"), 0.3333334);
			put(new Key<String>("N", "Pomme"),  0.3333333);
			put(new Key<String>("N", "Mouche"), 0.3333333);
			put(new Key<String>("N", "Montre"), 0.3333334);
			put(new Key<String>("D", "Une"),    1.0);
		}}, 
		new HashMap<String, Double>() {{
			put("P", 1.0);
		}},
		new ArrayList<String>() {{
			add("Je");
			add("Te");
			add("Montre");
			add("Ma");
			add("Montre");
		}});
		
		System.out.println(states);
*/
	}
}
