package viterbi;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static viterbi.Viterbi.viterbi;
import static viterbi.Viterbi.viterbiWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import viterbi.Viterbi.Key;
import viterbi.Viterbi.ViterbiMachine;
import viterbi.Viterbi.ViterbiModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
	
	@Test
	public void wrapperWithSampleFromPostagga() {
		List<String> states = viterbiWrapper(set("P", "V", "N", "D"),
				set("Je", "Te", "Ma", "Mange", "Tue", "Montre", "Pomme", "Mouche", "Une"),
				ViterbiTest.<Key<String>, Double>map(
						new Key<String>("P", "V"), 0.3333333,
						new Key<String>("P", "P"), 0.3333333,
						new Key<String>("P", "N"), 0.3333334,
						new Key<String>("V", "D"), 0.5,
						new Key<String>("V", "P"), 0.5,
						new Key<String>("D", "N"), 1.0
					),
				ViterbiTest.<Key<String>, Double>map(
						new Key<String>("P", "Je"), 0.3333333,
						new Key<String>("P", "Te"), 0.3333333,
						new Key<String>("P", "Ma"), 0.3333334,
						new Key<String>("V", "Mange"), 0.3333333,
						new Key<String>("V", "Tue"), 0.3333333,
						new Key<String>("V", "Montre"), 0.3333334,
						new Key<String>("N", "Pomme"), 0.3333333,
						new Key<String>("N", "Mouche"), 0.3333333,
						new Key<String>("N", "Montre"), 0.3333334,
						new Key<String>("D", "Une"), 1.0
					),
				ViterbiTest.<String, Double>map(
						"P", 1.0
					),
				list("Je", "Mange", "Une", "Pomme"));
		final List<String> expected = list("P", "V", "D", "N");
		assertThat(states, is(expected));
	}

	enum States {
		HEALTHY, FEVER
	};

	enum Observations {
		OK, COLD, DIZZY
	};

	@Test
	public void viterbiMachineWithSampleFromWikipedia() {
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
		List<States> states = machine.calculate();
		final List<States> expected = ImmutableList.of(States.HEALTHY, States.HEALTHY, States.FEVER);
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
