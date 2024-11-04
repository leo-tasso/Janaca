package it.unibo.ai.didattica.competition.tablut.janaca.utils;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.janaca.euristics.Euristics;

import java.util.Comparator;
import java.util.List;

public class ActionComparator {
    public static Comparator<Tuple<Action, State>> get(Euristics euristics, State.Turn turn) {
        return Comparator.comparing(p ->
                euristics.check(p.second(), turn));
    }
}
