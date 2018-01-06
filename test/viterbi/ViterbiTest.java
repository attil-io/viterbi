package viterbi;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import viterbi.Viterbi.ViterbiMachine;
import viterbi.Viterbi.ViterbiModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ViterbiTest {
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
	

	enum States { HEALTHY, FEVER };
	enum Observations { OK, COLD, DIZZY };

	@Test
	public void wikipediaSample() {
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
