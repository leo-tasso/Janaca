package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.Tuple;

import java.awt.Desktop;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MeasuresImpl implements  Measures {

    final private int amountRows;
    final private int amountCols;

    public MeasuresImpl(State stateFromPickGlobalInfo){
        this.amountRows = 9; //TODO - generalize it (maybe pick info smartly by State)
        this.amountCols = 9; //TODO - generalize it (maybe pick info smartly by State)
    }


    public record MeasurePayload(
            Set<Tuple<Integer,Integer>> whitePawn,
            Set<Tuple<Integer,Integer>> blackPawn,
            Optional<Tuple<Integer, Integer>> kingPawn){
    }


    private Tuple<Integer, Integer> getFromPos(Action toExtract){
        return new Tuple<>(toExtract.getRowFrom(), toExtract.getColumnFrom());
    }

    private Tuple<Integer, Integer> getToPos(Action toExtract){
        return new Tuple<>(toExtract.getRowTo(), toExtract.getColumnTo());
    }

    private MeasurePayload getNearby(State toExplore, Tuple<Integer, Integer> start){

        Iterator<Integer> dx = IntStream.rangeClosed(Math.max(0, start.first()-1), Math.min(amountRows-1, start.first()+1)).iterator();
        Iterator<Integer> dy = IntStream.rangeClosed(Math.max(0, start.first()-1), Math.min(amountCols-1, start.first()+1)).iterator();

//        Iterator<Integer> dx = IntStream.range(start.first() == 0 ? 0 : start.first() -1 , 1+(start.first() == amountRows-1 ? amountRows-1 : start.first()+1)).iterator();
//        Iterator<Integer> dy = IntStream.range(start.first() == 0 ? 0 : start.first() -1 , 1+(start.first() == amountRows-1 ? amountRows-1 : start.first()+1)).iterator();

//
//        Set<Pawn> toExtract = Stream.iterate(dx.next(), i -> dx.hasNext(), i -> dx.next())
//                .flatMap(i -> Stream.iterate(dy.next(), j -> dy.hasNext(), j -> dy.next()).map(j -> new Tuple<>(i,j)))
//                .map(pp -> toExplore.getPawn(pp.first(), pp.second())).collect(Collectors.toSet())

        var whitePawnSym = Pawn.WHITE;
        var blackPawnSym = Pawn.BLACK;
        var kingPawnSym = Pawn.KING;

        var tmp = Stream.iterate(dx.next(), i -> dx.hasNext(), i -> dx.next())
                .flatMap(i -> Stream.iterate(dy.next(), j -> dy.hasNext(), j -> dy.next()).map(j -> new Tuple<>(i,j)))
                .collect(Collectors.groupingBy(pp -> toExplore.getPawn(pp.first(), pp.second()))
                );
        return new MeasurePayload(
                new HashSet<>(tmp.getOrDefault(whitePawnSym,List.of())),
                new HashSet<>(tmp.getOrDefault(blackPawnSym,List.of())),
                Optional.ofNullable(tmp.getOrDefault(kingPawnSym,List.of()).isEmpty() ? tmp.get(kingPawnSym).getFirst() : null)
        );
    }

    private int exploreNearby(State actState, Tuple<Integer, Integer> start, boolean areAlly){
        if ((actState.getTurn() == State.Turn.WHITE && areAlly) || (actState.getTurn() == State.Turn.BLACK && !areAlly)){
            return this.getNearby(actState,start).whitePawn.size();
        } else {
            return this.getNearby(actState,start).blackPawn.size();
        }
    }

    @Override
    public int amountReachedAllies(State actState, Action myNewPos) {
        return this.exploreNearby(actState, this.getToPos(myNewPos), true);
    }

    @Override
    public int amountLeftAllies(State actState, Action myOldPos) {
        return this.exploreNearby(actState, this.getFromPos(myOldPos), true);
    }

    @Override
    public int amountReachedEnemys(State actState, Action myNewPos) {
        return this.exploreNearby(actState, this.getToPos(myNewPos), false);
    }

    @Override
    public int amountLeftEnemys(State actState, Action myOldPos) {
        return this.exploreNearby(actState, this.getFromPos(myOldPos), false);
    }

    @Override
    public int amountTravelledCells(State oldState, State acState, Pawn whoPawn) {
        return -1;
    }

    @Override
    public int amountTravelledCells(Action moveDone) {
        var start = this.getFromPos(moveDone);
        var end = this.getToPos(moveDone);
        int a = Math.abs(start.first()-end.first());
        int b = Math.abs(start.second()-end.second());
        return Math.max(a,b);
    }

    @Override
    public boolean haveKingOrtogonally(State actState) {
        return false;
    }

    @Override
    public boolean haveKingNearToMyLine(State actState) {
        return false;
    }

    @Override
    public boolean isKingReacheble(State acState) {
        return false;
    }

    @Override
    public int amountFarKing(State actState, Pawn whoPawn) {
        return 0;
    }

    @Override
    public int computeFarFromCenter(State acState, Pawn whoPawn) {
        return 0;
    }

    @Override
    public int canCaptureSomeone(State oldState, State acState) {
        return 0;
    }

    @Override
    public boolean isWarningPosition(State actState) {
        return false;
    }


}
