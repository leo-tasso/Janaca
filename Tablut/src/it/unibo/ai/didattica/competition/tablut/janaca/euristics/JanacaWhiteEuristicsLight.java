package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public class JanacaWhiteEuristicsLight implements TurnSpecificEuristics {
    Game game;

    //final Map<String,Double> reinforcedCoefficients = new HashMap<String,Double>();
    //final Map<String, Double> myCoefficients;

    public JanacaWhiteEuristicsLight(Game game) {
        this.game = game;
    }

    @Override
    public Double check(State position) {
        final MeasuresImpl measures = new MeasuresImpl(position);
        State newState;

        final double FRIENDILY_COEFF = 50.0;
        final double FEAR_COEFF = -50.0;
        final double COURAGE_COEFF = -100.0;
        final double CAPTURED_COEFF = 1000.0;

        final double DISCOVERING_PATH = 200.0;
        final double WARNING = -1000.0;
        final double ESCAPES = 1_000.0;

        //boolean haveMovedKing = position.getPawn(action.getRowFrom(),action.getColumnFrom()).equals(State.Pawn.KING);

        try {

            newState = position; //game.checkMove(position.clone(), action);

            if (newState.getTurn().equals(State.Turn.WHITEWIN)) {
                return Double.POSITIVE_INFINITY;
            }


            int before = 0; //measures.globalNearAllies(position, action);
            int next = 0;
//            int next = measures.globalNearAllies(newState);
            double friendily = 0.0; //FRIENDILY_COEFF * (next - before);

//            before = 0; //measures.globalNearEnemies(position, action);
//            next = measures.globalNearEnemies(newState);
            double enemies = 0.0; // FEAR_COEFF * (next - before);

            next = measures.leftAllies(newState);
            double allies = COURAGE_COEFF * next;

            before = 0; //measures.leftEnemies(position);
            next = measures.leftEnemies(newState);
            double captured = CAPTURED_COEFF * (next - before);

            //List<Double> myValues = new ArrayList<>(List.of(bias, friendily, enemies, captured));
            List<Double> myValues = new ArrayList<>(List.of(100000.0, friendily, allies, enemies, captured));

            //if (haveMovedKing){
//            before = measures.amountPotentialEscapes(position,game);
//            next = measures.amountPotentialEscapes(newState, game);
            double pot = 0.0; //DISCOVERING_PATH * (next - before);

//            before = 0; // measures.amountPotentialEscapes(position,game);
//            next = 0; //measures.amountRealEscapes(newState, game);
            double escapes = 0.0; //ESCAPES * (next - before);

            before = 0;
            next = measures.amountEnemiesNearKing(newState);
            double warning = WARNING * (next - before);

            myValues.addAll(List.of(pot,escapes, warning));
            //}


            //double allies = COURAGE_COEFF * measures.leftAllies(newState);
            //return allies; //Math.max(0, allies);

//            next = measures.leftEnemies(newState);
//            double captured = CAPTURED_COEFF * (next - before);

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
