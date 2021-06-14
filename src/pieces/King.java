package pieces;

import java.util.*;

import board.Board;
import board.Color;
import board.Cell;

import utils.Pair;

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? '♔' : '♚';
    }

    @Override
    public ArrayList<Pair> getBasicDeltas() {
        ArrayList<Pair> deltas = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (!(dx == 0 && dy == 0)) {
                    deltas.add(new Pair(dx, dy));
                }
            }
        }
        return deltas;
    }

    @Override
    public ArrayList<Cell> getAdditionalLegalMoves(Cell initial, Board board, Cell lastMove) {
        return new ArrayList<Cell>();
    }
}
