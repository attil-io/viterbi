package viterbi;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.immutableEnumMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;


public class Viterbi {

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
			
			try {
				possibleStates = ImmutableList.copyOf(getPossibleStates());
			} catch (IllegalStateException ise) {
				throw new IllegalArgumentException("empty states enum, or no explicit initial distribution provided", ise);
			}
			
			try {
				possibleObservations = ImmutableList.copyOf(getPossibleObservations());
			} catch (IllegalStateException ise) {
				throw new IllegalArgumentException("empty observations enum, or no explicit observations provided", ise);
			}
			
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
				throw new IllegalArgumentException("the sum of initial distributions should be 1.0, was " + sumInitProbs);
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
	
	
	enum State {
		HEALTHY, FEVER
	};

	enum Observation {
		OK, COLD, DIZZY
	};
	
	
	public static void main(String[] args) {
		ViterbiModel<State, Observation> model = ViterbiModel.<State, Observation>builder()
				.withInitialDistributions(ImmutableMap.<State, Double>builder()
						.put(State.HEALTHY, 0.6)
						.put(State.FEVER, 0.4)
						.build())
				.withTransitionProbability(State.HEALTHY, State.HEALTHY, 0.7)
				.withTransitionProbability(State.HEALTHY, State.FEVER, 0.3)
				.withTransitionProbability(State.FEVER, State.HEALTHY, 0.4)
				.withTransitionProbability(State.FEVER, State.FEVER, 0.6)
				.withEmissionProbability(State.HEALTHY, Observation.OK, 0.5)
				.withEmissionProbability(State.HEALTHY, Observation.COLD, 0.4)
				.withEmissionProbability(State.HEALTHY, Observation.DIZZY, 0.1)
				.withEmissionProbability(State.FEVER, Observation.OK, 0.1)
				.withEmissionProbability(State.FEVER, Observation.COLD, 0.3)
				.withEmissionProbability(State.FEVER, Observation.DIZZY, 0.6)
				.build();
		
		ImmutableList<Observation> observations = ImmutableList.of(Observation.OK, Observation.COLD, Observation.DIZZY);
		
		ViterbiMachine<State, Observation> machine = new ViterbiMachine<>(model, observations);
		System.out.println(machine.calculate());
	}
}
