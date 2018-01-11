package viterbi;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import viterbi.Viterbi.ViterbiMachine;
import viterbi.Viterbi.ViterbiModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ViterbiTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();	

	enum ZeroStatesZeroObservationsState { };
	enum ZeroStatesZeroObservationsObservation { };
	
	@Test
	public void zeroStatesZeroObservationsIsNotOk() {
		ViterbiModel<ZeroStatesZeroObservationsState, ZeroStatesZeroObservationsObservation> model = ViterbiModel.<ZeroStatesZeroObservationsState, ZeroStatesZeroObservationsObservation>builder()
				.withInitialDistributions(ImmutableMap.<ZeroStatesZeroObservationsState, Double>builder()
						.build())
				.build();
		
		ImmutableList<ZeroStatesZeroObservationsObservation> observations = ImmutableList.of();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty states enum, or no explicit initial distribution provided");
		new ViterbiMachine<>(model, observations);
	}
	
	enum ZeroStatesOneObservationState { };
	enum ZeroStatesOneObservationObservation { OBSERVATION0 };
	
	@Test
	public void zeroStatesOneObservationIsNotOk() {
		ViterbiModel<ZeroStatesOneObservationState, ZeroStatesOneObservationObservation> model = ViterbiModel.<ZeroStatesOneObservationState, ZeroStatesOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<ZeroStatesOneObservationState, Double>builder()
						.build())
				.build();
		
		ImmutableList<ZeroStatesOneObservationObservation> observations = ImmutableList.of();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty states enum, or no explicit initial distribution provided");
		new ViterbiMachine<>(model, observations);
	}

	enum OneStateZeroObservationsState { STATE0 };
	enum OneStateZeroObservationsObservation { };
	
	@Test
	public void oneStateZeroObservationsIsNotOk() {
		ViterbiModel<OneStateZeroObservationsState, OneStateZeroObservationsObservation> model = ViterbiModel.<OneStateZeroObservationsState, OneStateZeroObservationsObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateZeroObservationsState, Double>builder()
						.put(OneStateZeroObservationsState.STATE0, 1.0)
						.build())
				.build();
		
		ImmutableList<OneStateZeroObservationsObservation> observations = ImmutableList.of();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty observations enum, or no explicit observations provided");
		new ViterbiMachine<>(model, observations);
	}

	enum OneStateOneObservationState { STATE0 };
	enum OneStateOneObservationObservation { OBSERVATION0 };
	
	@Test
	public void oneStateOneObservationIsOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		ViterbiMachine<OneStateOneObservationState, OneStateOneObservationObservation> machine = new ViterbiMachine<>(model, observations);
		List<OneStateOneObservationState> states = machine.calculate();
		final List<OneStateOneObservationState> expected = ImmutableList.of(OneStateOneObservationState.STATE0);
		assertThat(states, is(expected));
	}

	@Test
	public void oneStateOneObservationMissingInitialDistributionIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty states enum, or no explicit initial distribution provided");
		new ViterbiMachine<>(model, observations);
	}
	
	@Test
	public void oneStateOneObservationMissingObservationsIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of();
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty observations enum, or no explicit observations provided");
		new ViterbiMachine<>(model, observations);
	}
	
	@Test
	public void oneStateOneObservationSumInitialDistribNotOneIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.1)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("the sum of initial distributions should be 1.0, was 1.1");
		new ViterbiMachine<>(model, observations);
	}

	@Test
	public void oneStateOneObservationNoTransitionProbabilitiesIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("at least one transition probability should be provided, 0 given");
		new ViterbiMachine<>(model, observations);
	}
	
	@Test
	public void oneStateOneObservationSumTransitionProbabilitiesNotOneIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.1)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("sum of transition probabilities for each state should be one, was 1.1 for state STATE0");
		new ViterbiMachine<>(model, observations);
	}

	@Test
	public void oneStateOneObservationZeroEmissionProbabilitiesIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("at least one emission probability should be provided, 0 given");
		new ViterbiMachine<>(model, observations);
	}
	
	@Test
	public void oneStateOneObservationSumEmissionProbabilitiesNotOneIsNotOk() {
		ViterbiModel<OneStateOneObservationState, OneStateOneObservationObservation> model = ViterbiModel.<OneStateOneObservationState, OneStateOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateOneObservationState, Double>builder()
						.put(OneStateOneObservationState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationState.STATE0, 1.0)
				.withEmissionProbability(OneStateOneObservationState.STATE0, OneStateOneObservationObservation.OBSERVATION0, 1.1)
				.build();
		
		ImmutableList<OneStateOneObservationObservation> observations = ImmutableList.of(OneStateOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("sum of emission probabilities for each state should be one, was 1.1 for state STATE0");
		new ViterbiMachine<>(model, observations);
	}

	enum OneStateTwoObservationsState { STATE0 };
	enum OneStateTwoObservationsObservation { OBSERVATION0, OBSERVATION1 };
	
	@Test
	public void oneStateTwoObservationsIsOk() {
		ViterbiModel<OneStateTwoObservationsState, OneStateTwoObservationsObservation> model = ViterbiModel.<OneStateTwoObservationsState, OneStateTwoObservationsObservation>builder()
				.withInitialDistributions(ImmutableMap.<OneStateTwoObservationsState, Double>builder()
						.put(OneStateTwoObservationsState.STATE0, 1.0)
						.build())
				.withTransitionProbability(OneStateTwoObservationsState.STATE0, OneStateTwoObservationsState.STATE0, 1.0)
				.withEmissionProbability(OneStateTwoObservationsState.STATE0, OneStateTwoObservationsObservation.OBSERVATION0, 0.4)
				.withEmissionProbability(OneStateTwoObservationsState.STATE0, OneStateTwoObservationsObservation.OBSERVATION1, 0.6)
				.build();
		
		ImmutableList<OneStateTwoObservationsObservation> observations = ImmutableList.of(OneStateTwoObservationsObservation.OBSERVATION1, OneStateTwoObservationsObservation.OBSERVATION1);
		
		ViterbiMachine<OneStateTwoObservationsState, OneStateTwoObservationsObservation> machine = new ViterbiMachine<>(model, observations);
		List<OneStateTwoObservationsState> states = machine.calculate();
		final List<OneStateTwoObservationsState> expected = ImmutableList.of(OneStateTwoObservationsState.STATE0, OneStateTwoObservationsState.STATE0);
		assertThat(states, is(expected));
	}

	enum TwoStatesOneObservationState { STATE0, STATE1 };
	enum TwoStatesOneObservationObservation { OBSERVATION0 };

	@Test
	public void twoStatesOneObservationIsOk() {
		ViterbiModel<TwoStatesOneObservationState, TwoStatesOneObservationObservation> model = ViterbiModel.<TwoStatesOneObservationState, TwoStatesOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<TwoStatesOneObservationState, Double>builder()
						.put(TwoStatesOneObservationState.STATE0, 0.6)
						.put(TwoStatesOneObservationState.STATE1, 0.4)
						.build())
				.withTransitionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationState.STATE0, 0.7)
				.withTransitionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationState.STATE1, 0.3)
				.withTransitionProbability(TwoStatesOneObservationState.STATE1, TwoStatesOneObservationState.STATE0, 0.4)
				.withTransitionProbability(TwoStatesOneObservationState.STATE1, TwoStatesOneObservationState.STATE1, 0.6)
				.withEmissionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationObservation.OBSERVATION0, 1.0)
				.withEmissionProbability(TwoStatesOneObservationState.STATE1, TwoStatesOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<TwoStatesOneObservationObservation> observations = ImmutableList.of(TwoStatesOneObservationObservation.OBSERVATION0, TwoStatesOneObservationObservation.OBSERVATION0);
		
		ViterbiMachine<TwoStatesOneObservationState, TwoStatesOneObservationObservation> machine = new ViterbiMachine<>(model, observations);
		List<TwoStatesOneObservationState> states = machine.calculate();
		final List<TwoStatesOneObservationState> expected = ImmutableList.of(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationState.STATE0);
		assertThat(states, is(expected));
	}
	
	@Test
	public void twoStatesOneObservationTransitionsOmittedForOneStateIsNotOk() {
		ViterbiModel<TwoStatesOneObservationState, TwoStatesOneObservationObservation> model = ViterbiModel.<TwoStatesOneObservationState, TwoStatesOneObservationObservation>builder()
				.withInitialDistributions(ImmutableMap.<TwoStatesOneObservationState, Double>builder()
						.put(TwoStatesOneObservationState.STATE0, 0.6)
						.put(TwoStatesOneObservationState.STATE1, 0.4)
						.build())
				.withTransitionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationState.STATE0, 0.7)
				.withTransitionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationState.STATE1, 0.3)
				.withEmissionProbability(TwoStatesOneObservationState.STATE0, TwoStatesOneObservationObservation.OBSERVATION0, 1.0)
				.withEmissionProbability(TwoStatesOneObservationState.STATE1, TwoStatesOneObservationObservation.OBSERVATION0, 1.0)
				.build();
		
		ImmutableList<TwoStatesOneObservationObservation> observations = ImmutableList.of(TwoStatesOneObservationObservation.OBSERVATION0, TwoStatesOneObservationObservation.OBSERVATION0);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("sum of transition probabilities for each state should be one, was 0.0 for state STATE1");
		new ViterbiMachine<>(model, observations);
	}

	enum TwoStatesTwoObservationsState { STATE0, STATE1 };
	enum TwoStatesTwoObservationsObservation { OBSERVATION0, OBSERVATION1 };

	@Test
	public void twoStatesTwoObservationsIsOk() {
		ViterbiModel<TwoStatesTwoObservationsState, TwoStatesTwoObservationsObservation> model = ViterbiModel.<TwoStatesTwoObservationsState, TwoStatesTwoObservationsObservation>builder()
				.withInitialDistributions(ImmutableMap.<TwoStatesTwoObservationsState, Double>builder()
						.put(TwoStatesTwoObservationsState.STATE0, 0.6)
						.put(TwoStatesTwoObservationsState.STATE1, 0.4)
						.build())
				.withTransitionProbability(TwoStatesTwoObservationsState.STATE0, TwoStatesTwoObservationsState.STATE0, 0.7)
				.withTransitionProbability(TwoStatesTwoObservationsState.STATE0, TwoStatesTwoObservationsState.STATE1, 0.3)
				.withTransitionProbability(TwoStatesTwoObservationsState.STATE1, TwoStatesTwoObservationsState.STATE0, 0.4)
				.withTransitionProbability(TwoStatesTwoObservationsState.STATE1, TwoStatesTwoObservationsState.STATE1, 0.6)
				.withEmissionProbability(TwoStatesTwoObservationsState.STATE0, TwoStatesTwoObservationsObservation.OBSERVATION0, 0.6)
				.withEmissionProbability(TwoStatesTwoObservationsState.STATE0, TwoStatesTwoObservationsObservation.OBSERVATION1, 0.4)
				.withEmissionProbability(TwoStatesTwoObservationsState.STATE1, TwoStatesTwoObservationsObservation.OBSERVATION0, 0.6)
				.withEmissionProbability(TwoStatesTwoObservationsState.STATE1, TwoStatesTwoObservationsObservation.OBSERVATION1, 0.4)
				.build();
		
		ImmutableList<TwoStatesTwoObservationsObservation> observations = ImmutableList.of(TwoStatesTwoObservationsObservation.OBSERVATION0, TwoStatesTwoObservationsObservation.OBSERVATION0);
		
		ViterbiMachine<TwoStatesTwoObservationsState, TwoStatesTwoObservationsObservation> machine = new ViterbiMachine<>(model, observations);
		List<TwoStatesTwoObservationsState> states = machine.calculate();
		final List<TwoStatesTwoObservationsState> expected = ImmutableList.of(TwoStatesTwoObservationsState.STATE0, TwoStatesTwoObservationsState.STATE0);
		assertThat(states, is(expected));
	}
	

	enum WikipediaState { HEALTHY, FEVER };
	enum WikipediaObservation { OK, COLD, DIZZY };

	@Test
	public void wikipediaSample() {
		ViterbiModel<WikipediaState, WikipediaObservation> model = ViterbiModel.<WikipediaState, WikipediaObservation>builder()
				.withInitialDistributions(ImmutableMap.<WikipediaState, Double>builder()
						.put(WikipediaState.HEALTHY, 0.6)
						.put(WikipediaState.FEVER, 0.4)
						.build())
				.withTransitionProbability(WikipediaState.HEALTHY, WikipediaState.HEALTHY, 0.7)
				.withTransitionProbability(WikipediaState.HEALTHY, WikipediaState.FEVER, 0.3)
				.withTransitionProbability(WikipediaState.FEVER, WikipediaState.HEALTHY, 0.4)
				.withTransitionProbability(WikipediaState.FEVER, WikipediaState.FEVER, 0.6)
				.withEmissionProbability(WikipediaState.HEALTHY, WikipediaObservation.OK, 0.5)
				.withEmissionProbability(WikipediaState.HEALTHY, WikipediaObservation.COLD, 0.4)
				.withEmissionProbability(WikipediaState.HEALTHY, WikipediaObservation.DIZZY, 0.1)
				.withEmissionProbability(WikipediaState.FEVER, WikipediaObservation.OK, 0.1)
				.withEmissionProbability(WikipediaState.FEVER, WikipediaObservation.COLD, 0.3)
				.withEmissionProbability(WikipediaState.FEVER, WikipediaObservation.DIZZY, 0.6)
				.build();
		
		ImmutableList<WikipediaObservation> observations = ImmutableList.of(WikipediaObservation.OK, WikipediaObservation.COLD, WikipediaObservation.DIZZY);
		
		ViterbiMachine<WikipediaState, WikipediaObservation> machine = new ViterbiMachine<>(model, observations);
		List<WikipediaState> states = machine.calculate();
		final List<WikipediaState> expected = ImmutableList.of(WikipediaState.HEALTHY, WikipediaState.HEALTHY, WikipediaState.FEVER);
		assertThat(states, is(expected));
	}
	
	enum PostaggaState { P, V, N, D };
	enum PostaggaObservation { Je, Te, Ma, Mange, Tue, Montre, Pomme, Mouche, Une };

	@Test
	public void postaggaSample() {
		ViterbiModel<PostaggaState, PostaggaObservation> model = ViterbiModel.<PostaggaState, PostaggaObservation>builder()
				.withInitialDistributions(ImmutableMap.<PostaggaState, Double>builder()
						.put(PostaggaState.P, 1.0)
						.put(PostaggaState.V, 0.0)
						.put(PostaggaState.N, 0.0)
						.put(PostaggaState.D, 0.0)
						.build())
				.withTransitionProbability(PostaggaState.P, PostaggaState.V, 0.3333333)
				.withTransitionProbability(PostaggaState.P, PostaggaState.P, 0.3333333)
				.withTransitionProbability(PostaggaState.P, PostaggaState.N, 0.3333334)
				.withTransitionProbability(PostaggaState.V, PostaggaState.D, 0.5)
				.withTransitionProbability(PostaggaState.V, PostaggaState.P, 0.5)
				.withTransitionProbability(PostaggaState.D, PostaggaState.N, 1.0)
				.withEmissionProbability(PostaggaState.P, PostaggaObservation.Je, 0.3333333)
				.withEmissionProbability(PostaggaState.P, PostaggaObservation.Te, 0.3333333)
				.withEmissionProbability(PostaggaState.P, PostaggaObservation.Ma, 0.3333334)
				.withEmissionProbability(PostaggaState.V, PostaggaObservation.Mange, 0.3333333)
				.withEmissionProbability(PostaggaState.V, PostaggaObservation.Tue, 0.3333333)
				.withEmissionProbability(PostaggaState.V, PostaggaObservation.Montre, 0.3333334)
				.withEmissionProbability(PostaggaState.N, PostaggaObservation.Pomme, 0.3333333)
				.withEmissionProbability(PostaggaState.N, PostaggaObservation.Mouche, 0.3333333)
				.withEmissionProbability(PostaggaState.N, PostaggaObservation.Montre, 0.3333334)
				.withEmissionProbability(PostaggaState.D, PostaggaObservation.Une, 1.0)
				.build();
		
		ImmutableList<PostaggaObservation> observations = ImmutableList.of(PostaggaObservation.Je, PostaggaObservation.Mange, PostaggaObservation.Une, PostaggaObservation.Pomme);
		
		ViterbiMachine<PostaggaState, PostaggaObservation> machine = new ViterbiMachine<>(model, observations);
		List<PostaggaState> states = machine.calculate();
		final List<PostaggaState> expected = ImmutableList.of(PostaggaState.P, PostaggaState.V, PostaggaState.D, PostaggaState.N);
		assertThat(states, is(expected));
	}
}
