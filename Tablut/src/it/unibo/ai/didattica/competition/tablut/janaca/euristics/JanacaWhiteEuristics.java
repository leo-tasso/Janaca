package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.lang.reflect.Method;
import java.util.*;

public class JanacaWhiteEuristics implements TurnSpecificEuristics {
    Game game;

    //final Map<String,Double> reinforcedCoefficients = new HashMap<String,Double>();
    //final Map<String, Double> myCoefficients;

    public JanacaWhiteEuristics(Game game) {
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
    public Double check(State position) {
        final MeasuresImpl measures = new MeasuresImpl(position);
        State newState;

        final double CAPTURED_COEFF = 1000.0;
        final double FRIENDILY_COEFF = 15.0;
        final double FEAR_COEFF = 100.0;
        final double COURAGE_COEFF = 70.0;

        final double DISCOVERING_PATH = 200.0;
        final double ESCAPES = 1_000.0;

        //boolean haveMovedKing = position.getPawn(action.getRowFrom(),action.getColumnFrom()).equals(State.Pawn.KING);

        try {
            newState = position; //game.checkMove(position.clone(), action);

            if (newState.getTurn().equals(State.Turn.WHITEWIN)) {
                return Double.POSITIVE_INFINITY;
            }

            //double bias = 10000;

            int before = 0; //measures.globalNearAllies(position, action);
            int next = measures.globalNearAllies(newState);
            double friendily = FRIENDILY_COEFF * (next - before);

            before = 0; //measures.globalNearEnemies(position, action);
            next = measures.globalNearEnemies(newState);
            double enemies = FEAR_COEFF * (next - before);

            next = measures.leftAllies(newState);
            double allies = COURAGE_COEFF * next;

            before = 0; //measures.leftEnemies(position);
            next = measures.leftEnemies(newState);
            double captured = CAPTURED_COEFF * (next - before);

            //List<Double> myValues = new ArrayList<>(List.of(bias, friendily, enemies, captured));
            List<Double> myValues = new ArrayList<>(List.of(friendily, enemies, captured));

            //if (haveMovedKing){
            before = measures.amountPotentialEscapes(position,game);
            next = measures.amountPotentialEscapes(newState, game);
            double pot = DISCOVERING_PATH * (next - before);

            before = 0; // measures.amountPotentialEscapes(position,game);
            next = 0; //measures.amountRealEscapes(newState, game);
            double escapes = ESCAPES * (next - before);

            myValues.addAll(List.of(pot,escapes));
            //}

            return Math.max(0, myValues.stream().reduce(Double::sum).orElseGet(() -> 0.0));

        } catch (Exception _) {
        }
        return Double.NEGATIVE_INFINITY;
    }

}



//            var mm = MeasuresOLD.class.getMethods();
//
//            for (Method m : mm){
//                if (MeasuresOLD.state_action.contains(m.getName())){
//                    m.invoke(measures,newState,action);
//                } else {
//                    m.invoke(measures,position,newState);
//                }
//
//            }
