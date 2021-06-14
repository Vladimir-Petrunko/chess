package pieces;

import java.util.*;

import board.Board;
import board.Color;
import board.Cell;

import utils.Pair;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? '♘' : '♞';
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    // TODO: write it in a more compact way
    public ArrayList<Pair> getBasicDeltas() {
        ArrayList<Pair> deltas = new ArrayList<>();
        deltas.add(new Pair(1, 2));
        deltas.add(new Pair(1, -2));
        deltas.add(new Pair(-1, 2));
        deltas.add(new Pair(-1, -2));
        deltas.add(new Pair(2, 1));
        deltas.add(new Pair(2, -1));
        deltas.add(new Pair(-2, 1));
        deltas.add(new Pair(-2, -1));
        return deltas;
    }

    @Override
    public ArrayList<Cell> getAdditionalLegalMoves(Cell initial, Board board, Cell lastMove) {
        return new ArrayList<Cell>();
    }
}
