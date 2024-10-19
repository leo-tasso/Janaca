package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;

public class JanacaBlackEuristics implements TurnSpecificEuristics {
    Game game;

    public JanacaBlackEuristics(Game game) {
        this.game = game;
    }
@Override
    public Double check(State position, Action action, List<State> pastStates) {
        State newState;
        try {
            newState = game.checkMove(position.clone(), action);
            if (newState.getTurn().equals(State.Turn.BLACKWIN)) return -100.0;
            return (double) (Euristics.countPieces(newState, State.Pawn.WHITE) + Euristics.countPieces(newState, State.Pawn.KING) * 10);
        } catch (Exception _) {
        }
        return Double.POSITIVE_INFINITY;
    }
}
