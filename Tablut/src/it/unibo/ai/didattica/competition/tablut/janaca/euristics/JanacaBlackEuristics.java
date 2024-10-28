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
        MeasuresImpl m = new MeasuresImpl(position);
        double value = 0.0;
        State newState;
        try {
            newState = game.checkMove(position.clone(), action);
            if (newState.getTurn().equals(State.Turn.BLACKWIN)){
                return Double.POSITIVE_INFINITY;
            }

            if(m.amountLeftAllies(position,action) > m.countPieces(newState,State.Pawn.BLACK)){

            }

            if(m.amountReachedEnemys(position,action)){

            }




            return (double) (m.countPieces(newState, State.Pawn.WHITE) + m.countPieces(newState, State.Pawn.KING) * 10);
        } catch (Exception _) {

        }
        return 0.0;
    }
}
