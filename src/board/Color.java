package board;

public enum Color {
    WHITE, BLACK;
    public Color getOppositeColor(Color color) {
        return (color == WHITE) ? BLACK : WHITE;
    }
}