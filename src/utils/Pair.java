package utils;

public class Pair {
    private final int first;
    private final int second;
    public Pair(final int first, final int second) {
        this.first = first;
        this.second = second;
    }
    public int first() {
        return first;
    }
    public int second() {
        return second;
    }
}