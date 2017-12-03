package viterbi;

import java.util.Stack;

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
		
		// ------------------------------------------------------------
		for (int i = 0; i < numStates; ++i) {
			System.out.print("state #" + i + ": ");
			for (int j = 0; j < observations.length; j++) {
				System.out.print(stateProbsForObservations[i][j] + " ");
			}
			System.out.println();
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
		
		Stack<Integer> stack = new Stack<>();
		
		for (int i = observations.length - 1; i >= 0; --i) {
			stack.push(stateWithMaxProb);
			stateWithMaxProb = prevStates[stateWithMaxProb][i];
		}
		
		int [] result = new int[observations.length];
		
		for (int i = 0; i < observations.length; ++i) {
			result[i] = stack.pop();
		}
		
		return result;
	}

	public static void main(String[] args) {
		double [] initialDistrib = {0.6, 0.4};
		double [][] transitionProbs = {{0.7, 0.3}, {0.4, 0.6}};
		double [][] emissionProbs = {{0.5, 0.4, 0.1}, {0.1, 0.3, 0.6}};
		int [] observations = {0, 1, 2};
		
		int [] states = viterbi(2, 3, initialDistrib, transitionProbs, emissionProbs, observations);
		for (int state: states) {
			System.out.println(state);
		}
	}
}
