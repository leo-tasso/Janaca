package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import java.util.List;

public class JanacaEuristics implements Euristics {
    private final TurnSpecificEuristics whiteEuristics;
    private final TurnSpecificEuristics blackEuristics;

    public JanacaEuristics(Game game) {
        this.whiteEuristics = new JanacaWhiteEuristics(game);
        this.blackEuristics = new JanacaBlackEuristics(game);
    }
    
    @Override
    public Double check(State position, State.Turn turn) {
        if(turn.equals(StateTablut.Turn.WHITE)){
            return this.whiteEuristics.check(position);
        }
        else if(turn.equals(StateTablut.Turn.BLACK)){
            return - this.blackEuristics.check(position);
        }
        else{
            throw new IllegalArgumentException("Invalid turn: " + position.getTurn());
        }
    }
}
