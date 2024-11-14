package it.unibo.ai.didattica.competition.tablut.janaca;

import it.unibo.ai.didattica.competition.tablut.janaca.euristics.JanacaEuristics;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.ActionComparator;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.ChildrenFinder;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.Tuple;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("ALL")
public class JanacaClient extends TablutClient {

    public static final int TOLLERANCE = 5000; //5 secs
    public static final int TAKE_BEST_MOVES_FACTOR = 1;
    public static final Optional<Integer> CUSTOM_TIMEOUT = Optional.of(150000);  //set to override server timeout (in seconds)

    private final int game;
    private Game rules = null;
    private JanacaEuristics euristics = null;
    private ChildrenFinder childrenFinder = null;
    private long timer = 0;
    private int timeout = 0;

    public JanacaClient(String player, String name, int gameChosen, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.game = gameChosen;
        this.timeout = CUSTOM_TIMEOUT.orElse(timeout) * 1000;
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
        childrenFinder = new ChildrenFinder(this.rules);

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
            System.out.println(state.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {

            }


            //if is my turn, i have to take a decision
            if (this.getPlayer().equals(state.getTurn())) {
                this.timer = System.currentTimeMillis();
                //Construct the set of actions
                ExecutorService executor = Executors.newSingleThreadExecutor();

                Set<Tuple<Action, State>>  possibleMoves = childrenFinder.find(state, state.getTurn());

                int depth = 0;
                Action a = null;
                try {
                    while (!this.timeEspired() && depth <4) { //TODO TO MODIFY TO SET DEPTH
                        int currentDepth = depth;
                        State finalState = state;
                        Future<Tuple<Action, Double>> futureTask = executor.submit(() -> minimax(finalState, possibleMoves, currentDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, finalState.getTurn()));
                        try {
                            // Wait for the task to complete within the time limit
                            Tuple<Action, Double> selectedActionWithEval = futureTask.get(this.leftTime(), TimeUnit.MILLISECONDS);
                            if (selectedActionWithEval != null) {
                                a = selectedActionWithEval.first();
                            }
                        } catch (TimeoutException e) {
                            futureTask.cancel(true); // Cancel the task if it takes too long
                            break;
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            break;
                        }

                        depth += 2;
                    }
                } finally {
                    executor.shutdownNow();
                }
                if (a == null) a = possibleMoves.stream().findFirst().get().first();
                System.out.println("Move selected: " + a.toString());
                try {
                    this.write(a);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }


            //CHECK STATUS GAME: if i lose/win/draw, game have to stop
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

    private Tuple<Action, Double> minimax(State position, Set<Tuple<Action, State>> actions, int depth, Double alpha, Double beta, StateTablut.Turn turn) {
        if (depth == 0 || (!position.getTurn().equals(State.Turn.BLACK) && !position.getTurn().equals(State.Turn.WHITE))) {
            Tuple<Action, State> move = null;
            if (position.getTurn().equals(StateTablut.Turn.WHITE)) {

                var debugList = actions.stream()
                        .sorted(Comparator.comparing(newStateTuple -> -this.euristics.check(newStateTuple.second(),StateTablut.Turn.WHITE)))
                        .map(newStateTuple -> new Tuple(newStateTuple, this.euristics.check(newStateTuple.second(),StateTablut.Turn.WHITE)))
                        .toList();

               move = actions.stream()
                .max(Comparator.comparing(newStateTuple -> this.euristics.check(newStateTuple.second(),StateTablut.Turn.WHITE)))
                .orElse(null);
            } else if (position.getTurn().equals(StateTablut.Turn.BLACK)) {


                var debugList = actions.stream()
                        .sorted(Comparator.comparing(newStateTuple -> this.euristics.check(newStateTuple.second(),StateTablut.Turn.BLACK)))
                        .map(newStateTuple -> new Tuple(newStateTuple, this.euristics.check(newStateTuple.second(),StateTablut.Turn.BLACK)))
                        .toList();

                move = actions.stream()
                .min(Comparator.comparing(newStateTuple -> this.euristics.check(newStateTuple.second(),StateTablut.Turn.BLACK)))
                .orElse(null);
            }
            if (move == null) {
                int debugVariable =0;
            }
            return new Tuple<>(move.first(), this.euristics.check(move.second(),position.getTurn()));
        }

        if (turn.equals(StateTablut.Turn.WHITE)) {
            Tuple<Action, Double> maxEval = new Tuple<>(null, Double.NEGATIVE_INFINITY);
            for (Tuple<Action, State> newState : SortActions(actions, turn)) {
                Tuple<Action, Double> branch = new Tuple<>(null, Double.NEGATIVE_INFINITY);
                if (!newState.second().getTurn().equals(State.Turn.BLACK) && !newState.second().getTurn().equals(State.Turn.WHITE)) {
                    branch = new Tuple<>(newState.first(), this.euristics.check(newState.second(),StateTablut.Turn.WHITE));
                } else {
                    branch = minimax(newState.second(), childrenFinder.find(newState.second(), StateTablut.Turn.BLACK), depth - 1, alpha, beta, StateTablut.Turn.BLACK);
                }
                if (branch.second() > maxEval.second()) maxEval = new Tuple<>(newState.first(), branch.second());
                alpha = Math.max(alpha, branch.second());
                if (beta <= alpha) break;
            }
            return maxEval;

        } else {
            Tuple<Action, Double> minEval = new Tuple<>(null, Double.POSITIVE_INFINITY);
            for (Tuple<Action, State> newState : SortActions(actions, turn)) {
                Tuple<Action, Double> branch = new Tuple<>(null, Double.POSITIVE_INFINITY);
                if (!newState.second().getTurn().equals(State.Turn.BLACK) && !newState.second().getTurn().equals(State.Turn.WHITE)) {
                    branch = new Tuple<>(newState.first(), this.euristics.check(newState.second(),StateTablut.Turn.BLACK));
                } else {
                    branch = minimax(newState.second(), childrenFinder.find(newState.second(), StateTablut.Turn.WHITE), depth - 1, alpha, beta, StateTablut.Turn.WHITE);
                }
                if (branch.second() < minEval.second()) minEval = new Tuple<>(newState.first(), branch.second());
                beta = Math.min(beta, branch.second());
                if (beta <= alpha) break;
            }
            return minEval;
        }

    }

    private List<Tuple<Action, State>> SortActions(Set<Tuple<Action, State>> newStates, StateTablut.Turn turn) {
        var debugList = newStates.stream()
                .sorted(turn.equals(State.Turn.WHITE) ?
                        ActionComparator.get(euristics,State.Turn.WHITE).reversed() :
                        ActionComparator.get(euristics, State.Turn.BLACK))
                .limit(newStates.size() / TAKE_BEST_MOVES_FACTOR)
                .map(t-> new Tuple(t,euristics.check(t.second(),State.Turn.WHITE)))
                .toList();

                return newStates.stream()
                .sorted(turn.equals(State.Turn.WHITE) ?
                        ActionComparator.get(euristics,State.Turn.WHITE).reversed() :
                        ActionComparator.get(euristics,State.Turn.BLACK))
                .limit(newStates.size() / TAKE_BEST_MOVES_FACTOR)
                .toList();

    }

    private boolean timeEspired() {
        return System.currentTimeMillis() - this.timer > this.timeout + TOLLERANCE;
    }

    private long leftTime() {
        return this.timeout - TOLLERANCE - System.currentTimeMillis() + this.timer;
    }


}
