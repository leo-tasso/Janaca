package it.unibo.ai.didattica.competition.tablut.Janaca;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;

public class JanacaWhiteEuristics implements Euristics {
    Game game;

    public JanacaWhiteEuristics(Game game) {
        this.game = game;
    }

    @Override
    public Double check(State position, Action action, List<State> pastStates) {
        State newState;
        try {
            newState = game.checkMove(position.clone(), action);
            if(newState.getTurn().equals(State.Turn.WHITEWIN)) return 10.0;
            return (double) (Euristics.countPieces(position, State.Pawn.BLACK) - Euristics.countPieces(newState, State.Pawn.BLACK))*2;
        }
        catch (Exception _){}
        return Double.NEGATIVE_INFINITY;
    }

}
