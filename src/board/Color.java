package board;

public enum Color {
    WHITE, BLACK;

    /**
     * Returns the opposite color to the given (either white or black).
     *
     * @param color a {@code Color}
     * @return the opposite color to {@code color}
     */
    public static Color getOppositeColor(Color color) {
        return color == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "white" : "black";
    }
}