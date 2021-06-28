package figureset;

import board.Color;

public class EnglishFigureSet implements FigureSet {
    @Override
    public char pawn(Color color) {
        return color == Color.WHITE ? 'P' : 'p';
    }

    @Override
    public char rook(Color color) {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public char knight(Color color) {
        return color == Color.WHITE ? 'N' : 'n';
    }

    @Override
    public char bishop(Color color) {
        return color == Color.WHITE ? 'B' : 'b';
    }

    @Override
    public char queen(Color color) {
        return color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public char king(Color color) {
        return color == Color.WHITE ? 'K' : 'k';
    }
}