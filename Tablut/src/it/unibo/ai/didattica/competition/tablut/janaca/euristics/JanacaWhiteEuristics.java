package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.JSONManager;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JanacaWhiteEuristics implements TurnSpecificEuristics {
    Game game;

    final Map<String,Double> reinforcedCoefficients = new HashMap<String,Double>();
    //final Map<String, Double> myCoefficients;


    public JanacaWhiteEuristics(Game game) {
        this.game = game;
        Arrays.stream(Measures.class.getMethods())
                .map(Method::getName)
                .peek(System.out::println)
                .forEach(name -> reinforcedCoefficients.put(name, 1.0));

        /**
         *
         * Insert here some intuition to initialite better reinforcedCoefficent
         *
         */

        String baseName = "janacaWhite";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String timestamp = now.format(formatter);
        String fileName = "weight/" + baseName + "_" + timestamp + ".json";

        // Create the "weight" directory if it doesn't exist
        File directory = new File("weight");
        if (!directory.exists()) {
            directory.mkdir();
        }

        JSONManager.byProgramToFile(this.reinforcedCoefficients, fileName);
    }

    @Override
    public Double check(State position, Action action, List<State> pastStates) {
        final MeasuresImpl m = new MeasuresImpl(position);

        State newState;
        try {
            newState = game.checkMove(position.clone(), action);
            if (newState.getTurn().equals(State.Turn.WHITEWIN)) {
                return 100.0;
            }
            return (double) -Euristics.countPieces(newState, State.Pawn.BLACK);
        } catch (Exception _) {
        }
        return Double.NEGATIVE_INFINITY;
    }

}
