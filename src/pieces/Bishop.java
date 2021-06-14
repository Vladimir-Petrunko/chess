package pieces;

import java.util.*;

import board.Board;
import board.Color;
import board.Cell;

import utils.Pair;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? '♗' : '♝';
    }

    @Override
    // TODO: get rid of constants in loop
    public ArrayList<Pair> getBasicDeltas() {
        ArrayList<Pair> deltas = new ArrayList<>();
        for (int dx = -7; dx <= 7; dx++) {
            deltas.add(new Pair(dx, dx));
            deltas.add(new Pair(dx, -dx));
        }
        return deltas;
    }

    @Override
    public ArrayList<Cell> getAdditionalLegalMoves(Cell initial, Board board, Cell lastMove) {
        return new ArrayList<Cell>();
    }
}
