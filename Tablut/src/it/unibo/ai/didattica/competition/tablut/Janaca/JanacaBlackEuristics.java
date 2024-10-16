package it.unibo.ai.didattica.competition.tablut.Janaca;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;

public class JanacaBlackEuristics implements Euristics {
    Game game;

    public JanacaBlackEuristics(Game game) {
        this.game = game;
    }

    @Override
    public Double check(State position, Action action, List<State> pastStates) {
        State newState;
        try {
            newState = game.checkMove(position.clone(), action);
            if(newState.getTurn().equals(State.Turn.BLACKWIN)) return  -10.0;
            return (double) (Euristics.countPieces(newState, State.Pawn.WHITE)*2 + Euristics.countPieces(newState, State.Pawn.KING)*2 - Euristics.countPieces(position, State.Pawn.KING) - Euristics.countPieces(position, State.Pawn.WHITE));
        } catch (Exception _) {
        }
        return Double.POSITIVE_INFINITY;
    }
}
