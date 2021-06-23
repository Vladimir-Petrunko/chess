package figureset;

import board.Color;

public class FigurineFigureSet implements FigureSet {
    @Override
    public char pawn(final Color color) {
        return color == Color.WHITE ? '♙' : '♟';
    }

    @Override
    public char rook(final Color color) {
        return color == Color.WHITE ? '♖' : '♜';
    }

    @Override
    public char knight(final Color color) {
        return color == Color.WHITE ? '♘' : '♞';
    }

    @Override
    public char bishop(final Color color) {
        return color == Color.WHITE ? '♗' : '♝';
    }

    @Override
    public char queen(final Color color) {
        return color == Color.WHITE ? '♕' : '♛';
    }

    @Override
    public char king(final Color color) {
        return color == Color.WHITE ? '♔' : '♚';
    }
}