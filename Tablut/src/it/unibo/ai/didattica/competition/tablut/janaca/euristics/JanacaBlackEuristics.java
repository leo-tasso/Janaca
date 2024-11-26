package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class JanacaBlackEuristics implements TurnSpecificEuristics {
    Game game;

    // Weights
    private final double draw = 4000.0;
    private final double alliesPawns = 110.0;
    private final double enemiesPawns = 100.0;
    private final double alliesNearKing = 105.0;
    private final double potentialEscapes = 200.0;


    public JanacaBlackEuristics(Game game) {
        this.game = game;
    }

    @Override
    public Double check(State position) {
        MeasuresImpl m = new MeasuresImpl(position);
        double value = 10000.0;

        try {
            if (position.getTurn().equals(State.Turn.BLACKWIN)){
                return Double.POSITIVE_INFINITY;
            }else if(position.getTurn().equals(State.Turn.WHITEWIN)){
                return Double.NEGATIVE_INFINITY;
            }

            if(position.getTurn().equals(State.Turn.DRAW)){
                value -= this.draw;
            }

            value -= m.leftAllies(position) * this.alliesPawns;
            value += m.leftEnemies(position) * this.enemiesPawns;
            value += m.amountAlliesNearKing(position) * this.alliesNearKing;
            value -= m.amountPotentialEscapes(position,game) * this.potentialEscapes;

            return value;
        } catch (Exception _) {
            return 0.0;
        }
    }
}