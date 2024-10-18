package it.unibo.ai.didattica.competition.tablut.Janaca;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("ALL")
public class JanacaClient extends TablutClient {

    private final int game;
    Game rules = null;
    JanacaEuristics euristics = null;

    List<State> pastStates = new ArrayList<>();

    public JanacaClient(String player, String name, int gameChosen, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.game = gameChosen;
    }

    public JanacaClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, name, 4, timeout, ipAddress);
    }

    public JanacaClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "random", 4, timeout, ipAddress);
    }

    public JanacaClient(String player) throws UnknownHostException, IOException {
        this(player, "random", 4, 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int gameType = 4;
        String role = "";
        String name = "Janaca";
        String ipAddress = "localhost";
        int timeout = 60;
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            System.out.println(args[1]);
            timeout = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            ipAddress = args[2];
        }
        System.out.println("Selected client: " + args[0]);

        JanacaClient client = new JanacaClient(role, name, gameType, timeout, ipAddress);
        client.run();
    }

    @Override
    public void run() {

//Print name of team
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        State state;
//Set Game rules
        switch (this.game) {
            case 1, 3:
                this.rules = new GameTablut();
                break;
            case 2:
                this.rules = new GameModernTablut();
                break;
            case 4:
                this.rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
                System.out.println("Ashton Tablut game");
                break;
            default:
                System.out.println("Error in game selection");
                System.exit(4);
        }
        euristics = new JanacaEuristics(this.rules);


        System.out.println("You are player " + this.getPlayer().toString() + "!");

        //Game Loop
        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state:");
            state = this.getCurrentState();
            pastStates.add(state);
            System.out.println(state.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {
            }


            if (this.getPlayer().equals(state.getTurn())) {

                //Construct the set of actions
                Set<Action> possibleMoves = FindChildren(state, state.getTurn());

                var selectedActionWithEval = minimax(state, possibleMoves, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, state.getTurn());
                Action a = selectedActionWithEval.first();
                System.out.println("Move selected: " + a.toString());
                try {
                    this.write(a);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }

            if (this.getPlayer().equals(State.Turn.WHITE)) {
                // If white player, is my turn
                if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } else {
                if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }
            }
        }
    }

    private Tuple<Action, Double> minimax(State position, Set<Action> actions, int depth, Double alpha, Double beta, StateTablut.Turn turn) {
        if (depth == 0 || (!position.getTurn().equals(State.Turn.BLACK) && !position.getTurn().equals(State.Turn.WHITE))) {
            Action move = null;
            if (turn.equals(StateTablut.Turn.WHITE)) {
                move = actions.stream()
                        .max(Comparator.comparing(action -> this.euristics.check(position.clone(), action, turn, pastStates)))
                        .orElse(null);
            } else {
                move = actions.stream()
                        .min(Comparator.comparing(action -> this.euristics.check(position.clone(), action, turn, pastStates)))
                        .orElse(null);
            }
            return new Tuple<>(move, this.euristics.check(position.clone(), move, turn, pastStates));
        }

        if (turn.equals(StateTablut.Turn.WHITE)) {
            Tuple<Action, Double> maxEval = new Tuple<>(null, Double.NEGATIVE_INFINITY);
            for (Action action : actions) {
                State newState = null;
                try {
                    newState = this.rules.checkMove(position.clone(), action);
                } catch (Exception _) {
                    //if move not legal exception is thrown and the move is not added
                }

                var branch = minimax(newState, FindChildren(newState, StateTablut.Turn.BLACK), depth - 1, alpha, beta, StateTablut.Turn.BLACK);
                if (branch.second() > maxEval.second()) maxEval = new Tuple<>(action, branch.second());
                alpha = Math.max(alpha, branch.second());
                if (beta <= alpha) break;
            }
            return maxEval;

        } else {
            Tuple<Action, Double> minEval = new Tuple<>(null, Double.POSITIVE_INFINITY);
            for (Action action : actions) {
                State newState = null;
                try {
                    newState = this.rules.checkMove(position.clone(), action);
                } catch (Exception _) {
                    //if move not legal exception is thrown and the move is not added
                }

                var branch = minimax(newState, FindChildren(newState, StateTablut.Turn.WHITE), depth - 1, alpha, beta, StateTablut.Turn.WHITE);
                if (branch.second() < minEval.second()) minEval = new Tuple<>(action, branch.second());
                beta = Math.min(beta, branch.second());
                if (beta <= alpha) break;
            }
            return minEval;
        }

    }

    //Returns a set of all the possible actions given a state
    private Set<Action> FindChildren(State state, StateTablut.Turn turn) {
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
                String from = this.getCurrentState().getBox(pawn[0], pawn[1]);
                String to = this.getCurrentState().getBox(emptyBox[0], emptyBox[1]);
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


    public record Tuple<A, B>(A first, B second) {

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

}
