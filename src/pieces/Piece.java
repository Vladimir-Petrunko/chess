package pieces;

import board.Board;
import board.Cell;
import board.Color;

import utils.Pair;

import java.util.ArrayList;

public abstract class Piece {
    protected final Color color;
    private boolean hasMoved = false;

    public Piece(final Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void move() {
        hasMoved = true;
    }

    public boolean canJump() {
        return false;
    }

    public abstract char getSymbol();
    public abstract ArrayList<Pair> getBasicDeltas();

    public ArrayList<Cell> getBasicLegalMoves(Cell initial, Board board) {
        ArrayList<Cell> moves = new ArrayList<>();
        ArrayList<Pair> deltas = getBasicDeltas();
        for (Pair delta : deltas) {
            int dx = delta.first();
            int dy = delta.second();
            Cell shifted = initial.shift(dx, dy);
            boolean legal = shifted.withinBounds() && board.canOccupy(shifted, this);
            if (!canJump()) {
                legal &= board.isFreePathBetween(initial, shifted);
            }
            if (legal) {
                moves.add(shifted);
            }
        }
        return moves;
    }

    public abstract ArrayList<Cell> getAdditionalLegalMoves(Cell initial, Board board, Cell lastMove);

    public ArrayList<Cell> getLegalMoves(Cell initial, Board board, Cell lastMove) {
        ArrayList<Cell> moves = new ArrayList<>();
        ArrayList<Cell> basic = getBasicLegalMoves(initial, board);
        moves.addAll(basic);
        ArrayList<Cell> additional = getAdditionalLegalMoves(initial, board, lastMove);
        moves.addAll(additional);
        return moves;
    }
}