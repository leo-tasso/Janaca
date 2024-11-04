package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Set;

public interface Measures {

    Set<String> nearness = Set.of("globalNearAllies", "globalNearEnemies");

    Set<String> left = Set.of("leftEnemies");

    int globalNearAllies(State actState);

    int globalNearEnemies(State actState);

    int leftAllies(State actState);

    int leftEnemies(State actState);

    int amountPotentialEscapes(State actState, Game rules);

    int amountRealEscapes(State actState, Game rules);

//    int globalNearAllies(State actState, Action pos);
//
//    int globalNearEnemies(State actState, Action pos);
//
//    int leftEnemies(State actState);
//
//    int amountPotentialEscapes(State actState, Game rules);
//
//    int amountRealEscapes(State actState, Game rules);



    //int hidden
}
