package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;


public interface Measures {

    /**
     * Compute how many allies i have adiacent on my cell
     * @param acState
     * @param myNewPos
     * @return
     */
    public int amountReachedAllies(State acState, Pawn myNewPos);

    /**
     * Compute how many allies i left leaving my old position
     * @param acState
     * @param myNewPos
     * @return
     */
    public int amountLeftAllies(State acState, Pawn myOldPos);

    /**
     * Compute how many enemy i have adiacent on my cell
     * @param acState
     * @param myNewPos
     * @return
     */
    public int amountReachedEnemys(State acState, Pawn myNewPos);

    /**
     * Compute how many enemys i left leaving my old position
     * @param acState
     * @param myOldPos
     * @return
     */
    public int amountLeftEnemys(State acState, Pawn myOldPos);

    /**
     * Compute how long was my trip
     * @param oldState
     * @param acState
     * @param whoPawn
     * @return
     */
    public int amountTravelledCells(State oldState, State acState, Pawn whoPawn);

    /**
     * Compute how long was my trip
     * @param moveDone
     * @return
     */
    public int amountTravelledCells(Action moveDone);

    /**
     * Check if i have the King Pawn on my vertical-hortogonal line
     * @param actState
     * @return
     */
    public boolean haveKingOrtogonally(State actState);

    /**
     * Check if i can eventually go near to the king, without necessarly decide to go near to him
     * @param actState
     * @return
     */
    public boolean haveKingNearToMyLine(State actState);

    /**
     * Check if i can actually go near to the king
     * @param acState
     * @return
     */
    public boolean isKingReacheble(State acState);

    /**
     * Compute how far is king 
     * @param actState
     * @param whoPawn
     * @return
     */
    public int amountFarKing(State actState, Pawn whoPawn);


    /**
     * 
     * @param acState
     * @param whoPawn
     * @return
     */
    public int computeFarFromCenter(State acState, Pawn whoPawn);

    /**
     * Compute if attemp this move i can capture enemy pawns
     * @param oldState
     * @param acState
     * @return amount of them (0 if not, n if there are almost 1)
     */
    public int canCaptureSomeone(State oldState, State acState);

    /**
     * Compute if my enemy can capture me
     * @param actState
     * @return
     */
    public boolean isWarningPosition(State actState);
    
    
}
