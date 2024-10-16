package it.unibo.ai.didattica.competition.tablut.Janaca;

import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.util.List;

public class JanacaEuristics {
    private final Euristics whiteEuristics;
    private final Euristics blackEuristics;

    public JanacaEuristics(Game game) {
        this.whiteEuristics = new JanacaWhiteEuristics(game);
        this.blackEuristics = new JanacaBlackEuristics(game);
    }

    public Double check(State position, Action action, StateTablut.Turn turn, List<State> pastStates){
        if(turn.equals(StateTablut.Turn.WHITE)){
            return this.whiteEuristics.check(position, action, pastStates);
        }
        else if(turn.equals(StateTablut.Turn.BLACK)){
            return this.blackEuristics.check(position, action, pastStates);
        }
        else{
            throw new IllegalArgumentException("Invalid turn: " + turn);
        }
    }
}
