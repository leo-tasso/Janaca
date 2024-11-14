package it.unibo.ai.didattica.competition.tablut.janaca.euristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.janaca.utils.Tuple;

import java.io.IOException;
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


    private Integer countGlobalNearby(State actState, Pawn source, Pawn toConsider) {
        return Stream.iterate(0, i -> i < amountRows, i -> i+1)
                .flatMap(i -> Stream.iterate(0, ii -> ii < amountCols, ii -> ii+1).map(j -> new Tuple<Integer, Integer>(i, j))) //create all positions
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
    public int globalNearAllies(State actState) {
        Pawn source = actState.getTurn().equals(State.Turn.WHITE) ? Pawn.WHITE : Pawn.BLACK;
        Pawn toConsider = source;
        return this.countGlobalNearby(actState, source, toConsider);
    }

    @Override
    public int globalNearEnemies(State actState) {
        Pawn source = actState.getTurn().equals(State.Turn.WHITE) ? Pawn.WHITE : Pawn.BLACK;
        Pawn toConsider = (source == Pawn.WHITE) ? Pawn.BLACK : Pawn.WHITE;
        return this.countGlobalNearby(actState, source, toConsider);
    }



    @Override
    public int leftEnemies(State actState) {
        State.Turn turn = actState.getTurn();
        //only because in the new state "turn is swapped"
        return turn.equals(State.Turn.BLACK) ?  this.amountBlack - countPieces(actState, Pawn.BLACK) : this.amountWhite - countPieces(actState, Pawn.WHITE);
    }

    @Override
    public int leftAllies(State actState) {
        State.Turn turn = actState.getTurn();
        return turn.equals(State.Turn.BLACK) ? this.amountWhite - countPieces(actState, Pawn.WHITE) : this.amountBlack - countPieces(actState, Pawn.BLACK);
    }


    private int countPieces(State position, State.Pawn pawnType) {
        return (int) Arrays.stream(position.getBoard())
                .flatMap(Arrays::stream)
                .filter(pawn -> pawn == pawnType)
                .count();
    }



    private List<Tuple<Integer, Integer>> moveToExtremis(Tuple<Integer, Integer> start){
        Tuple<Integer, Integer> a = new Tuple<>(start.first(), 0);
        Tuple<Integer, Integer> b = new Tuple<>(start.first(), amountCols-1);
        Tuple<Integer, Integer> c = new Tuple<>(0, start.second());
        Tuple<Integer, Integer> d = new Tuple<>(amountRows-1, start.second());

        return List.of(a, b, c, d);
    }

    @Override
    public int amountPotentialEscapes(State actState, Game rules) {
//        var tmpState = actState.clone();
//        var tmpBoard = tmpState.getBoard();
//        nord_sud_ovest_est(kingPos,false)
//                .forEach(
//                        column -> column.forEach(pp -> tmpBoard[pp.first()][pp.second()] = Pawn.EMPTY)
//                );
//        tmpState.setBoard(tmpBoard);
//
//        return this.amountRealEscapes(tmpState, rules);
        var kingPos = this.getKingPosition(actState);
        var interesting = Set.of(1,7);
        var interesting2 = Set.of(2,6);
        var row = interesting.contains(kingPos.first()) ? 1 : (interesting2.contains(kingPos.first()) ? 2 : 0);
        var cols = interesting.contains(kingPos.second()) ? 1 : (interesting2.contains(kingPos.second()) ? 2 : 0);
        return row+cols;
    }

    @Override
    public int amountRealEscapes(State actState, Game rules) {
        /*
        var kingPos = this.getKingPosition(actState);
        return moveToExtremis(kingPos).stream().mapToInt(pp -> {

            Action a = null;
            try {
                a = new Action(
                        actState.getBox(kingPos.first(), kingPos.second()),
                        actState.getBox(pp.first(), pp.second()),
                        actState.getTurn()); //TODO - chiedere conferma
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                rules.checkMove(actState.clone(), a);
                return 1;
            } catch (Exception e){
                return 0;
            }
        }).sum();*/
        return 0;
    }


//



    private MeasurePayload getNearby(State toExplore, Tuple<Integer, Integer> start) {

        //Iterator<Integer> dx = Stream.of(-1, 1).map(ddx -> start.first()).filter(i -> i>=0 && i<amountRows).iterator();
        //Iterator<Integer> dy = Stream.of(-1, 1).map(ddy -> start.second()).filter(i -> i>=0 && i<amountCols).iterator();
//      IntStream.rangeClosed(Math.max(0, start.first() - 1), Math.min(amountRows - 1, start.first() + 1)).iterator();
//      Iterator<Integer> dy = IntStream.rangeClosed(Math.max(0, start.first() - 1), Math.min(amountCols - 1, start.first() + 1)).iterator();

        var row = start.first();
        var col = start.second();

        List<Tuple<Integer, Integer>> directions = List.of(
          new Tuple<>(row, col - 1),
          new Tuple<>(row, col + 1),
          new Tuple<>(row - 1, col),
          new Tuple<>(row + 1, col)
        );

        var whitePawnSym = Pawn.WHITE;
        var blackPawnSym = Pawn.BLACK;
        var kingPawnSym = Pawn.KING;

        Map<Pawn, List<Tuple<Integer,Integer>>> tmp = directions.stream()
                .filter(pos -> pos.first() >= 0 && pos.first() < amountRows && pos.second() >= 0 && pos.second() < amountCols)
                .collect(Collectors.groupingBy(pos -> toExplore.getPawn(pos.first(),pos.second())));

        /*var tmp = Stream.iterate(dx.next(), i -> dx.hasNext(), i -> dx.next())
                .flatMap(i -> Stream.iterate(dy.next(), j -> dy.hasNext(), j -> dy.next()).map(j -> new Tuple<>(i, j)))
                .collect(Collectors.groupingBy(pp -> toExplore.getPawn(pp.first(), pp.second()))
                );*/
        return new MeasurePayload(
                new HashSet<>(tmp.getOrDefault(whitePawnSym, List.of())),
                new HashSet<>(tmp.getOrDefault(blackPawnSym, List.of())),
                Optional.ofNullable(
                        tmp.getOrDefault(kingPawnSym, List.of()).isEmpty()
                                ? null
                                : tmp.get(kingPawnSym).getFirst()
                )
        );
    }

    private Tuple<Integer, Integer> getKingPosition(State actState){
        return Stream.iterate(0, i -> i < amountRows, i -> i+1)
                .flatMap(i -> Stream.iterate(0, ii -> ii < amountCols, ii -> ii+1).map(j -> new Tuple<Integer, Integer>(i, j))) //create all positions
                .filter( pp -> actState.getPawn(pp.first(), pp.second()) == Pawn.KING) // seleziono solo il re
                .findFirst().orElse(new Tuple<>(0,0));
    }


    public int amountAlliesNearKing(State actState){
        return this.exploreNearby(actState, this.getKingPosition(actState), true);
    }

    public int amountEnemiesNearKing(State actState){
        return this.exploreNearby(actState, this.getKingPosition(actState), false);
    }



    /////////////////////// OLD STUFF ////////////////////////

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

    private int exploreNearby(State actState, Tuple<Integer, Integer> start, boolean areAlly) {
        if ((actState.getTurn() == State.Turn.WHITE && areAlly) || (actState.getTurn() == State.Turn.BLACK && !areAlly)) {
            return this.getNearby(actState, start).blackPawn.size();
        } else {
            return this.getNearby(actState, start).whitePawn.size();
        }
    }

    private Stream<Tuple<Integer, Integer>> obtainColumn(Tuple<Integer, Integer> start, boolean isHorrizontal, boolean isIncreasing, boolean keepStart){
        int axisToModify = isHorrizontal ? start.first() : start.second();

        return Stream.iterate(
                axisToModify,
                i -> (i >= 0) && (isHorrizontal ? i < amountCols : i < amountRows),
                i -> isIncreasing ? i+1 : i-1
        ).map(axis -> isHorrizontal
                ? new Tuple<>(start.first(), axis)
                : new Tuple<>(axis , start.second())
        ).skip(keepStart ? 0 : 1);
    }

    private Stream<Stream<Tuple<Integer, Integer>>> nord_sud_ovest_est(Tuple<Integer, Integer> start, boolean keepStart){
        var nord = obtainColumn(start,false,false,keepStart);
        var sud = obtainColumn(start,false,true,keepStart);
        var ovest = obtainColumn(start,true,false,keepStart);
        var est  = obtainColumn(start,true,true,keepStart);
        return Stream.of(nord, sud, ovest, est);
    }


//    @Override
//    public int amountPotentialEscapes(State actState) {
//        var kingPos = getKingPosition(actState);
//        return nord_sud_ovest_est(kingPos, false)
//                .mapToInt( column -> column.dropWhile() )
//                .sum();
//    }
//
//    @Override
//    public int amountRealEscapes(State actState) {
//        var kingPos = getKingPosition(actState);
//
//        return nord_sud_ovest_est(kingPos, false)
//                .mapToInt( column -> column.dropWhile(pp -> actState.getPawn(pp.first(),pp.second())) )
//                .sum();
//    }



}