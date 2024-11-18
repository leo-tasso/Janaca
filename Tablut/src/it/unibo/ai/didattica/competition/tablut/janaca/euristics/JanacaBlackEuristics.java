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

            value += m.leftEnemies(position) * 100;
            value -= m.leftAllies(position) * 120;

            value += m.amountAlliesNearKing(position) * 100;

            value -= m.amountPotentialEscapes(position,game) * 200;


            return value;
        } catch (Exception _) {
            return 0.0;
        }
    }
}