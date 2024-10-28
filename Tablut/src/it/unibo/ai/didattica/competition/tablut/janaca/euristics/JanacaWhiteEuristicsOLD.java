package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class JanacaWhiteEuristicsOLD implements TurnSpecificEuristics {
    Game game;

    //final Map<String,Double> reinforcedCoefficients = new HashMap<String,Double>();
    //final Map<String, Double> myCoefficients;

    public JanacaWhiteEuristicsOLD(Game game) {
        this.game = game;

//        Arrays.stream(Measures.class.getMethods())
//                .map(Method::getName)
//                .peek(System.out::println)
//                .forEach(name -> reinforcedCoefficients.put(name, 1.0));
        //MeasuresOLD.state_action.stream().forEach(m -> {myCoefficients.put(m,1.0); return;});

        /**
         *
         * Insert here some intuition to initialite better reinforcedCoefficent
         *
         */
        //myCoefficients = List.of();

    }

    @Override
    public Double check(State position, Action action, List<State> pastStates) {
        final MeasuresImpl measures = new MeasuresImpl(position);
        State newState;

        try {
            newState = game.checkMove(position.clone(), action);

            var mm = MeasuresOLD.class.getMethods();

            for (Method m : mm){
                if (MeasuresOLD.state_action.contains(m.getName())){
                    m.invoke(measures,newState,action);
                } else {
                    m.invoke(measures,position,newState);
                }

            }

            if (newState.getTurn().equals(State.Turn.WHITEWIN)) {
                return 100.0;
            }
            return (double) 1.0; //-Euristics.countPieces(newState, State.Pawn.BLACK);
        } catch (Exception _) {
        }
        return Double.NEGATIVE_INFINITY;
    }

}
