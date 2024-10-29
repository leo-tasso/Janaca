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

@SuppressWarnings("ALL")
public class JanacaClient extends TablutClient {

    public static final int TOLLERANCE = 5000; //5 secs
    public static final int TAKE_BEST_MOVES_FACTOR = 3;
    public static final Optional<Integer> CUSTOM_TIMEOUT = Optional.empty();  //Optional.of(5);  //set to override server timeout (in seconds)

    private final int game;
    private Game rules = null;
    private JanacaEuristics euristics = null;
    private ChildrenFinder childrenFinder = null;
    private long timer = 0;
    private int timeout = 0;
    private List<State> pastStates = new ArrayList<>();

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
            pastStates.add(state);
            System.out.println(state.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {

            }


            //if is my turn, i have to take a decision
            if (this.getPlayer().equals(state.getTurn())) {
                this.timer = System.currentTimeMillis();
                //Construct the set of actions
                Set<Action> possibleMoves = childrenFinder.find(state, state.getTurn());

                int depth = 0;
                Action a = null;
                while (!timeEspired()) {
                    var selectedActionWithEval = minimax(state, possibleMoves, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, state.getTurn());
                    if ((!timeEspired() || a == null) && selectedActionWithEval != null)
                        a = selectedActionWithEval.first();
                    depth += 2;
                }
                if (a == null) a = possibleMoves.stream().findFirst().get();
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

    private Tuple<Action, Double> minimax(State position, Set<Action> actions, int depth, Double alpha, Double beta, StateTablut.Turn turn) {
        if (depth == 0 || (!position.getTurn().equals(State.Turn.BLACK) && !position.getTurn().equals(State.Turn.WHITE)) || timeEspired()) {
            Action move = null;
            if (position.getTurn().equals(StateTablut.Turn.WHITE)) {
                var sortedActions = actions.stream()
                        .sorted((Comparator.comparing(action -> -this.euristics.check(position.clone(), action, turn, pastStates))))
                        .map(action -> new Tuple<Action, Double>(action, this.euristics.check(position.clone(), action, turn, pastStates)))
                        .toList();
                move = sortedActions.get(0).first();
                //.max(Comparator.comparing(action -> this.euristics.check(position.clone(), action, turn, pastStates)))
                //.orElse(null);
            } else if (position.getTurn().equals(StateTablut.Turn.BLACK)) {
                var sortedActions = actions.stream()
                        .sorted((Comparator.comparing(action -> this.euristics.check(position.clone(), action, turn, pastStates))))
                        .map(action -> new Tuple<Action, Double>(action, this.euristics.check(position.clone(), action, turn, pastStates)))
                        .toList();
                move = sortedActions.get(0).first();
                //.min(Comparator.comparing(action -> this.euristics.check(position.clone(), action, turn, pastStates)))
                //.orElse(null);
            }
            return new Tuple<>(move, this.euristics.check(position.clone(), move, turn, pastStates));
        }

        if (turn.equals(StateTablut.Turn.WHITE)) {
            Tuple<Action, Double> maxEval = new Tuple<>(null, Double.NEGATIVE_INFINITY);
            for (Action action : SortActions(position, actions, turn)) {
                State newState = null;
                try {
                    newState = this.rules.checkMove(position.clone(), action);
                } catch (Exception _) {
                    //if move not legal exception is thrown and the move is not added
                }
                Tuple<Action, Double> branch = new Tuple<>(null, Double.NEGATIVE_INFINITY);
                if (!newState.getTurn().equals(State.Turn.BLACK) && !newState.getTurn().equals(State.Turn.WHITE)) {
                    branch = new Tuple<>(action, this.euristics.check(newState, action, turn, pastStates));
                } else {
                    branch = minimax(newState, childrenFinder.find(newState, StateTablut.Turn.BLACK), depth - 1, alpha, beta, StateTablut.Turn.BLACK);
                }
                if (branch.second() > maxEval.second()) maxEval = new Tuple<>(action, branch.second());
                alpha = Math.max(alpha, branch.second());
                if (beta <= alpha) break;
            }
            return maxEval;

        } else {
            Tuple<Action, Double> minEval = new Tuple<>(null, Double.POSITIVE_INFINITY);
            for (Action action : SortActions(position, actions, turn)) {
                State newState = null;
                try {
                    newState = this.rules.checkMove(position.clone(), action);
                } catch (Exception _) {
                    //if move not legal exception is thrown and the move is not added
                }
                Tuple<Action, Double> branch = new Tuple<>(null, Double.POSITIVE_INFINITY);
                if (!newState.getTurn().equals(State.Turn.BLACK) && !newState.getTurn().equals(State.Turn.WHITE)) {
                    branch = new Tuple<>(action, this.euristics.check(newState, action, turn, pastStates));
                } else {
                    branch = minimax(newState, childrenFinder.find(newState, StateTablut.Turn.WHITE), depth - 1, alpha, beta, StateTablut.Turn.WHITE);
                }
                if (branch.second() < minEval.second()) minEval = new Tuple<>(action, branch.second());
                beta = Math.min(beta, branch.second());
                if (beta <= alpha) break;
            }
            return minEval;
        }

    }

    private List<Action> SortActions(State position, Set<Action> actions, StateTablut.Turn turn) {
        return actions.stream()
                .sorted(turn.equals(State.Turn.WHITE) ?
                        ActionComparator.get(position, turn, euristics, pastStates).reversed() :
                        ActionComparator.get(position, turn, euristics, pastStates))
                .limit(actions.size() / TAKE_BEST_MOVES_FACTOR)
                .toList();
    }

    private boolean timeEspired() {
        return System.currentTimeMillis() - this.timer > this.timeout + TOLLERANCE;
    }


}
