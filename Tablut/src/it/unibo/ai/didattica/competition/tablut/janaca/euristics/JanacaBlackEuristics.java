package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class JanacaBlackEuristics implements TurnSpecificEuristics {
    public static final double DECREASING_COEFF = 0.9;
    Game game;

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
            }

            if(position.getTurn().equals(State.Turn.DRAW)){
                value -= 4000;
            }
            value -= m.leftAllies(position) * 150;

            value += (150 * (1 - Math.pow(DECREASING_COEFF, m.leftEnemies(position))) / (1 - DECREASING_COEFF));;
            if(m.leftEnemies(position)!=0){
                int debugVariable = 0;
            }

            value += m.amountAlliesNearKing(position) * 100;

            value -= m.amountPotentialEscapes(position,game) * 200;


            return value;
        } catch (Exception _) {
            return 0.0;
        }
    }
}