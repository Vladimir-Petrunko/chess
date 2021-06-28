package figureset;

import board.Color;

public class FigurineFigureSet implements FigureSet {
    @Override
    public char pawn(Color color) {
        return color == Color.WHITE ? '♙' : '♟';
    }

    @Override
    public char rook(Color color) {
        return color == Color.WHITE ? '♖' : '♜';
    }

    @Override
    public char knight(Color color) {
        return color == Color.WHITE ? '♘' : '♞';
    }

    @Override
    public char bishop(Color color) {
        return color == Color.WHITE ? '♗' : '♝';
    }

    @Override
    public char queen(Color color) {
        return color == Color.WHITE ? '♕' : '♛';
    }

    @Override
    public char king(Color color) {
        return color == Color.WHITE ? '♔' : '♚';
    }
}