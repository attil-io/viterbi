package viterbi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	
	public static void main(String[] args) {
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
	}
}
