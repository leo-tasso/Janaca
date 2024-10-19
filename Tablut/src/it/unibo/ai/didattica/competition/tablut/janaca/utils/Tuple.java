package it.unibo.ai.didattica.competition.tablut.janaca.utils;

public record Tuple<A, B>(A first, B second) {

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}