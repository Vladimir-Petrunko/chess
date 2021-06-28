package board;

public enum Color {
    WHITE, BLACK;

    public static Color getOppositeColor(Color color) {
        return color == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "white" : "black";
    }
}