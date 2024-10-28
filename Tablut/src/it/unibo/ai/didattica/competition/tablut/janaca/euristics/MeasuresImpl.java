package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.Tuple;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeasuresImpl implements Measures {

    final private int amountRows;
    final private int amountCols;
    final private int amountWhite;
    final private int amountBlack;

    public MeasuresImpl(State stateFromPickGlobalInfo) {
        this.amountRows = 9; //TODO - generalize it (maybe pick info smartly by State)
        this.amountCols = 9; //TODO - generalize it (maybe pick info smartly by State)
        this.amountWhite = 9; //TODO - generalize it (maybe pick info smartly by State)
        this.amountBlack = 16; //TODO - generalize it (maybe pick info smartly by State)
    }

    public record MeasurePayload(
            Set<Tuple<Integer, Integer>> whitePawn,
            Set<Tuple<Integer, Integer>> blackPawn,
            Optional<Tuple<Integer, Integer>> kingPawn) {
    }


    @Override
    public int globalNearAllies(State actState, Action pos) {
        Pawn source = actState.getTurn().equals(State.Turn.WHITE) ? Pawn.WHITE : Pawn.BLACK;
        Pawn toConsider = source;
        return countGlobalNearby(actState, source, toConsider);
    }

    @Override
    public int globalNearEnemies(State actState, Action pos) {
        Pawn source = actState.getTurn().equals(State.Turn.WHITE) ? Pawn.WHITE : Pawn.BLACK;
        Pawn toConsider = (source == Pawn.WHITE) ? Pawn.BLACK : Pawn.WHITE;
        return countGlobalNearby(actState, source, toConsider);
    }

    private Integer countGlobalNearby(State actState, Pawn source, Pawn toConsider) {
        return Stream.iterate(0, i -> i < amountRows, i -> i++)
                .flatMap(i -> Stream.iterate(0, ii -> ii < amountCols, ii -> ii++).map(j -> new Tuple<Integer, Integer>(i, j))) //create all positions
                .filter( //hold only "here i am"
                        pp ->
                                source == Pawn.WHITE
                                        ? Set.of(Pawn.WHITE, Pawn.KING).contains(actState.getPawn(pp.first(), pp.second())) //if i am white i have to consider king, too
                                        : actState.getPawn(pp.first(), pp.second()) == Pawn.BLACK //if i am black i have to consider only black
                )
                .map(pp -> getNearby(actState, pp)) //obtain the "payload" of adiacent cells
                .map(
                        payload -> toConsider == Pawn.WHITE //if i have to counter white
                                ? (payload.whitePawn.size() + (payload.kingPawn.isPresent() ? 1 : 0)) //i extract all white
                                : payload.blackPawn.size()) //black otherwise
                .reduce(Integer::sum).get();
    }




    @Override
    public int leftEnemies(State actState) {
        State.Turn turn = actState.getTurn();
        return turn.equals(State.Turn.BLACK) ? countPieces(actState, Pawn.WHITE) : countPieces(actState, Pawn.BLACK);
    }

    private int countPieces(State position, State.Pawn pawnType) {
        return (int) Arrays.stream(position.getBoard())
                .flatMap(Arrays::stream)
                .filter(pawn -> pawn == pawnType)
                .count();
    }

    private Tuple<Integer, Integer> getFromPos(Action toExtract) {
        return new Tuple<>(toExtract.getRowFrom(), toExtract.getColumnFrom());
    }

    private Tuple<Integer, Integer> getToPos(Action toExtract) {
        return new Tuple<>(toExtract.getRowTo(), toExtract.getColumnTo());
    }

    private MeasurePayload getNearby(State toExplore, Tuple<Integer, Integer> start) {

        Iterator<Integer> dx = Stream.of(-1, 1).map(ddx -> start.first() + ddx).filter(i -> i>=0 || i<amountRows).iterator();
        Iterator<Integer> dy = Stream.of(-1, 1).map(ddy -> start.second() + ddy).filter(i -> i>=0 || i<amountRows).iterator();
//      IntStream.rangeClosed(Math.max(0, start.first() - 1), Math.min(amountRows - 1, start.first() + 1)).iterator();
//      Iterator<Integer> dy = IntStream.rangeClosed(Math.max(0, start.first() - 1), Math.min(amountCols - 1, start.first() + 1)).iterator();

        var whitePawnSym = Pawn.WHITE;
        var blackPawnSym = Pawn.BLACK;
        var kingPawnSym = Pawn.KING;

        var tmp = Stream.iterate(dx.next(), i -> dx.hasNext(), i -> dx.next())
                .flatMap(i -> Stream.iterate(dy.next(), j -> dy.hasNext(), j -> dy.next()).map(j -> new Tuple<>(i, j)))
                .collect(Collectors.groupingBy(pp -> toExplore.getPawn(pp.first(), pp.second()))
                );
        return new MeasurePayload(
                new HashSet<>(tmp.getOrDefault(whitePawnSym, List.of())),
                new HashSet<>(tmp.getOrDefault(blackPawnSym, List.of())),
                Optional.ofNullable(
                        tmp.getOrDefault(kingPawnSym, List.of()).isEmpty()
                                ? tmp.get(kingPawnSym).getFirst()
                                : null
                )
        );
    }

    private Tuple<Integer, Integer> getKingPosition(State actState){
        return Stream.iterate(0, i -> i < amountRows, i -> i++)
                .flatMap(i -> Stream.iterate(0, ii -> ii < amountCols, ii -> ii++).map(j -> new Tuple<Integer, Integer>(i, j))) //create all positions
                .filter( pp -> actState.getPawn(pp.first(), pp.second()) == Pawn.KING) // seleziono solo il re
                .findFirst().orElse(new Tuple<>(0,0));
    }

    private int exploreNearby(State actState, Tuple<Integer, Integer> start, boolean areAlly) {
        if ((actState.getTurn() == State.Turn.WHITE && areAlly) || (actState.getTurn() == State.Turn.BLACK && !areAlly)) {
            return this.getNearby(actState, start).whitePawn.size();
        } else {
            return this.getNearby(actState, start).blackPawn.size();
        }
    }

    public int amountAlliesNearKing(State actState){
        return this.exploreNearby(actState, this.getKingPosition(actState), true);
    }

    public int amountEnemiesNearKing(State actState){
        return this.exploreNearby(actState, this.getKingPosition(actState), false);
    }


//    public int amountReachedAllies(State actState, Action myNewPos) {
//        return this.exploreNearby(actState, this.getToPos(myNewPos), true);
//    }
//
//    public int amountLeftAllies(State actState, Action myOldPos) {
//        return this.exploreNearby(actState, this.getFromPos(myOldPos), true);
//    }
//
//    public int amountReachedEnemy(State actState, Action myNewPos) {
//        return this.exploreNearby(actState, this.getToPos(myNewPos), false);
//    }
//
//    public int amountLeftEnemy(State actState, Action myOldPos) {
//        return this.exploreNearby(actState, this.getFromPos(myOldPos), false);
//    }





}