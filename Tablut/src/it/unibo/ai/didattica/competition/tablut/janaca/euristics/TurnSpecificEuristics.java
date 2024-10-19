package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;

public interface TurnSpecificEuristics {
    Double check(State position, Action action, List<State> pastStates);
}
