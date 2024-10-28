package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Set;


public interface MeasuresOLD {


    public final Set<String> state_action = Set.of("amountReachedAllies","amountLeftAllies","amountReachedEnemys", "amountLeftEnemys");
    public final Set<String> state_state = Set.of("capturedSomeone");


    /**
     * Compute how many allies i have adiacent on my cell
     * @param actState
     * @param myNewPos
     * @return
     */
    public int amountReachedAllies(State actState, Action myNewPos);

    /**
     * Compute how many allies i left leaving my old position
     * @param actState
     * @param myOldPos
     * @return
     */
    public int amountLeftAllies(State actState, Action myOldPos);

    /**
     * Compute how many enemy i have adiacent on my cell
     * @param actState
     * @param myNewPos
     * @return
     */
    public int amountReachedEnemys(State actState, Action myNewPos);

    /**
     * Compute how many enemys i left leaving my old position
     * @param actState
     * @param myOldPos
     * @return
     */
    public int amountLeftEnemys(State actState, Action myOldPos);

    /**
     * Compute if attemp this move i can capture enemy pawns
     * @param oldState
     * @param actState
     * @return amount of them (0 if not, n if there are almost 1)
     */
    public int leftEnemies(State oldState, State actState);

//    /**
//     * Compute how long was my trip
//     * @param moveDone
//     * @return
//     */
//    public int amountTravelledCells(Action moveDone);


//    /**
//     * Compute how long was my trip
//     * @param oldState
//     * @param actState
//     * @param whoPawn
//     * @return
//     */
//    public int amountTravelledCells(State oldState, State actState, Pawn whoPawn);


//    /**
//     * Check if i have the King Pawn on my vertical-hortogonal line
//     * @param actState
//     * @return
//     */
//    public boolean haveKingOrtogonally(State actState);
//
//    /**
//     * Check if i can eventually go near to the king, without necessarly decide to go near to him
//     * @param actState
//     * @return
//     */
//    public boolean haveKingNearToMyLine(State actState);
//
//    /**
//     * Check if i can actually go near to the king
//     * @param actState
//     * @return
//     */
//    public boolean isKingReacheble(State actState);
//
//    /**
//     * Compute how far is king
//     * @param actState
//     * @param whoPawn
//     * @return
//     */
//    public int amountFarKing(State actState, Pawn whoPawn);


//    /**
//     *
//     * @param actState
//     * @param whoPawn
//     * @return
//     */
//    public int computeFarFromCenter(State actState, Pawn whoPawn);

//    /**
//     * Compute if my enemy can capture me
//     * @param actState
//     * @return
//     */
//    public boolean isWarningPosition(State actState);


//    /**
//     * Compute the amount of obstacled cells
//     * @param actState
//     * @return
//     */
//    public int amountHiddenCells(State actState);


}
