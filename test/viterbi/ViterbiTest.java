package viterbi;

import static viterbi.Viterbi.viterbi;
import static viterbi.Viterbi.viterbiWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;

import viterbi.Viterbi.Key;
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

	@Test
	public void oneStateOneObservation() {
		double [] initialDistrib = {1.0};
		double [][] transitionProbs = {{1.0}};
		double [][] emissionProbs = {{1.0}};
		int [] observations = {0};
		
		final int [] states = viterbi(1, 1, initialDistrib, transitionProbs, emissionProbs, observations);
		final int [] expected = {0};
		assertThat(states, is(expected));
	}

	@Test
	public void oneStateTwoObservations() {
		double [] initialDistrib = {1.0};
		double [][] transitionProbs = {{1.0}};
		double [][] emissionProbs = {{0.4, 0.5}};
		int [] observations = {1, 1};
		
		final int [] states = viterbi(1, 2, initialDistrib, transitionProbs, emissionProbs, observations);
		final int [] expected = {0, 0};
		assertThat(states, is(expected));
	}

	@Test
	public void twoStatesOneObservation() {
		double [] initialDistrib = {0.6, 0.4};
		double [][] transitionProbs = {{0.7, 0.3}, {0.4, 0.6}};
		double [][] emissionProbs = {{1.0}, {1.0}};
		int [] observations = {0, 0};
		
		final int [] states = viterbi(2, 1, initialDistrib, transitionProbs, emissionProbs, observations);
		final int [] expected = {0, 0};
		assertThat(states, is(expected));
	}

	@Test
	public void twoStatesTwoObservations() {
		double [] initialDistrib = {0.6, 0.4};
		double [][] transitionProbs = {{0.7, 0.3}, {0.4, 0.6}};
		double [][] emissionProbs = {{0.6, 0.4}, {0.6, 0.4}};
		int [] observations = {0, 0};
		
		final int [] states = viterbi(2, 2, initialDistrib, transitionProbs, emissionProbs, observations);
		final int [] expected = {0, 0};
		assertThat(states, is(expected));
	}
	
	@Test
	public void wrapperWithSampleFromWikipedia() {
		List<String> states = viterbiWrapper(set("Healthy", "Fever"),
				set("Normal", "Cold", "Dizzy"),
				ViterbiTest.<Key<String>, Double>map(
						new Key<String>("Healthy", "Healthy"), 0.7,
						new Key<String>("Healthy", "Fever"), 0.3,
						new Key<String>("Fever", "Healthy"), 0.4,
						new Key<String>("Fever", "Fever"), 0.6
					),
				ViterbiTest.<Key<String>, Double>map(
						new Key<String>("Healthy", "Normal"), 0.5,
						new Key<String>("Healthy", "Cold"), 0.4,
						new Key<String>("Healthy", "Dizzy"), 0.1,
						new Key<String>("Fever", "Normal"), 0.1,
						new Key<String>("Fever", "Cold"), 0.3,
						new Key<String>("Fever", "Dizzy"), 0.6
					),
				ViterbiTest.<String, Double>map(
						"Healthy", 0.6,
						"Fever", 0.4
					),
				list("Normal", "Cold", "Dizzy"));
		final List<String> expected = list("Healthy", "Healthy", "Fever");
		assertThat(states, is(expected));
	}
	
	
	private static <T> Set<T> set(T ... elements) {
		Set<T> ret = new HashSet<>();
		for (T e: elements) {
			ret.add(e);
		}
		return ret;
	}
	
	private static <K, V> Map<K, V> map(Object ... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("keysAndValues.length = " + keysAndValues.length);
		}
		Map<K, V> result = new HashMap<K, V>();
		for (int i = 0; i < keysAndValues.length - 1; i += 2) {
			result.put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
		}
		return result;
	}
	
	private static <T> List<T> list(T ... elements) {
		return Arrays.asList(elements);
	}

}
