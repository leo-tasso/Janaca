package it.unibo.ai.didattica.competition.tablut.Janaca;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Arrays;
import java.util.List;

public interface Euristics {
    Double check(State position, Action action, List<State> pastStates);

    static long countPieces(State position, State.Pawn pawnType) {
        return Arrays.stream(position.getBoard())
                .flatMap(Arrays::stream)
                .filter(pawn -> pawn == pawnType)
                .count();
    }
}
