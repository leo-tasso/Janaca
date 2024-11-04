package it.unibo.ai.didattica.competition.tablut.janaca.utils;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildrenFinder {
    private final Game rules;

    public ChildrenFinder(Game rules) {
        this.rules = rules;
    }

    public Set<Tuple<Action, State>> find(State state, StateTablut.Turn turn) {
        List<int[]> pawns = new ArrayList<>();
        Set<Tuple<Action, State>> possibleMoves = new HashSet<>();        // If the game is not running, i.e. it's neither black nor white turn, no moves are possible
        if (!state.getTurn().equals(State.Turn.BLACK) && !state.getTurn().equals(State.Turn.WHITE))
            return possibleMoves;

        // Collect positions of pawns based on the player's turn
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                // Check for white or king pawns
                if (turn.equals(StateTablut.Turn.WHITE) &&
                        (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString()) ||
                                state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString()))) {
                    pawns.add(new int[]{i, j});
                }
                // Check for black pawns
                else if (turn.equals(StateTablut.Turn.BLACK) &&
                        state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                    pawns.add(new int[]{i, j});
                }
            }
        }

        // Parallelize the loop for each pawn using parallelStream
        pawns.forEach(pawn -> {
            int row = pawn[0];
            int col = pawn[1];
            String from = state.getBox(row, col);

            // Check moves in all four directions
            checkDirection(state, turn, from, row, col, -1, 0, possibleMoves); // Move up
            checkDirection(state, turn, from, row, col, 1, 0, possibleMoves);  // Move down
            checkDirection(state, turn, from, row, col, 0, -1, possibleMoves); // Move left
            checkDirection(state, turn, from, row, col, 0, 1, possibleMoves);  // Move right
            if (possibleMoves.isEmpty()){
                int debugVariable = 0;
            }
        });

        return possibleMoves;
    }

    private void checkDirection(State state, StateTablut.Turn turn, String from, int row, int col, int rowDelta, int colDelta, Set<Tuple<Action, State>>  possibleMoves) {
        int newRow = row + rowDelta;
        int newCol = col + colDelta;

        // Continue moving in the direction until we hit the edge of the board or a blocked square
        while (newRow >= 0 && newRow < state.getBoard().length && newCol >= 0 && newCol < state.getBoard().length) {
            if (!state.getPawn(newRow, newCol).equalsPawn(State.Pawn.EMPTY.toString())) break;

            String to = state.getBox(newRow, newCol);
            try {
                Action move = new Action(from, to, turn);
                State newState = this.rules.checkMove(state.clone(), move);
                possibleMoves.add(new Tuple<>(move,newState));
            } catch (Exception e) {
                break; // Stop further checks in this direction if a move fails
            }

            // Move to the next square in the same direction
            newRow += rowDelta;
            newCol += colDelta;
        }
    }
}
