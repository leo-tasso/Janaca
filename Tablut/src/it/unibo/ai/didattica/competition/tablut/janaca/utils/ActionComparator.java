package it.unibo.ai.didattica.competition.tablut.janaca.utils;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.janaca.euristics.Euristics;

import java.util.Comparator;
import java.util.List;

public class ActionComparator {
    public static Comparator<Action> get(State position, StateTablut.Turn turn, Euristics euristics, List<State> pastStates) {
        return Comparator.comparing(action ->
                euristics.check(position.clone(), action,turn, pastStates));
    }
}
