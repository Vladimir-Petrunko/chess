package figureset;

import board.Color;

public class EnglishFigureSet implements FigureSet {
    @Override
    public char pawn(final Color color) {
        return color == Color.WHITE ? 'P' : 'p';
    }

    @Override
    public char rook(final Color color) {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public char knight(final Color color) {
        return color == Color.WHITE ? 'N' : 'n';
    }

    @Override
    public char bishop(final Color color) {
        return color == Color.WHITE ? 'B' : 'b';
    }

    @Override
    public char queen(final Color color) {
        return color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public char king(final Color color) {
        return color == Color.WHITE ? 'K' : 'k';
    }
}