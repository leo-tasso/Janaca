package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Arrays;
import java.util.List;

public interface Euristics {
    Double check(State position, Action action, State.Turn turn, List<State> pastStates);

    static long countPieces(State position, State.Pawn pawnType) {
        return Arrays.stream(position.getBoard())
                .flatMap(Arrays::stream)
                .filter(pawn -> pawn == pawnType)
                .count();
    }
}

