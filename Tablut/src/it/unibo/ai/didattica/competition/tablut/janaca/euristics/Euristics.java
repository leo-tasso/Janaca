package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Arrays;
import java.util.List;

public interface Euristics {
    Double check(State position, State.Turn turn);

}

