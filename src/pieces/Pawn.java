package pieces;

import java.util.*;

import board.Board;
import board.Color;
import board.Cell;

import utils.Pair;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? '♙' : '♟';
    }

    @Override
    // TODO: get rid of constants in loop
    public ArrayList<Pair> getBasicDeltas() {
        ArrayList<Pair> deltas = new ArrayList<>();
        if (color == Color.BLACK) {
            deltas.add(new Pair(1, 0));
        } else {
            deltas.add(new Pair(-1, 0));
        }
        return deltas;
    }

    @Override
    public ArrayList<Cell> getAdditionalLegalMoves(Cell initial, Board board, Cell lastMove) {
        return new ArrayList<Cell>();
    }
}
