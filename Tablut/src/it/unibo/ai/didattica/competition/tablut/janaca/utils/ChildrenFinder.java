package it.unibo.ai.didattica.competition.tablut.janaca.utils;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildrenFinder {
    private final Game rules;

    public ChildrenFinder(Game rules) {
        this.rules = rules;
    }

    public Set<Action> find(State state, StateTablut.Turn turn) {
        List<int[]> pawns = new ArrayList<>();
        List<int[]> empty = new ArrayList<>();
        Set<Action> possibleMoves = new HashSet<>();
        if (!state.getTurn().equals(State.Turn.BLACK) && !state.getTurn().equals(State.Turn.WHITE)) return possibleMoves;
        // Collect positions of pawns and empty boxes based on the player's turn
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
                // Check for empty spaces
                if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                    empty.add(new int[]{i, j});
                }
            }
        }


        // Generate possible moves
        for (int[] pawn : pawns) {
            for (int[] emptyBox : empty.stream().filter(e -> e[0] == pawn[0] || e[1] == pawn[1]).toList()) {
                String from = state.getBox(pawn[0], pawn[1]);
                String to = state.getBox(emptyBox[0], emptyBox[1]);
                try {
                    Action move = new Action(from, to, turn);
                    this.rules.checkMove(state.clone(), move);
                    possibleMoves.add(move);
                } catch (IOException e) {
                    e.printStackTrace(); // Handle IOException
                } catch (Exception e) {
                    // Move not legal, exception caught
                }
            }
        }

        return possibleMoves;
    }
}
