package viterbi;

import static viterbi.Viterbi.viterbi;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class ViterbiTest {

	@Test
	public void sampleCaseShouldYieldGoodResults() {
		double [] initialDistrib = {0.6, 0.4};
		double [][] transitionProbs = {{0.7, 0.3}, {0.4, 0.6}};
		double [][] emissionProbs = {{0.5, 0.4, 0.1}, {0.1, 0.3, 0.6}};
		int [] observations = {0, 1, 2};
		
		final int [] states = viterbi(2, 3, initialDistrib, transitionProbs, emissionProbs, observations);
		final int [] expected = {0, 0, 1};
		assertThat(states, is(expected));
	}

}