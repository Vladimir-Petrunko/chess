package pieces;

import board.Board.Position;
import board.Cell;
import board.Color;
import board.Move;
import utils.Pair;
import java.util.List;
import java.util.ArrayList;
import figureset.FigureSetManager;

public abstract class Piece {
    protected final Color color;

    public Piece(final Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean canJump() {
        return false;
    }

    public char getSymbol() {
        return FigureSetManager.getSymbol(this);
    }

    public abstract boolean validMoveDelta(final int dr, final int dc);

    public boolean validAttackDelta(final int dr, final int dc) {
        return validMoveDelta(dr, dc);
    }

    public abstract List<Pair> getBasicDeltas();

    public List<Move> getBasicLegalMoves(final Cell initial, final Position position) {
        List<Move> moves = new ArrayList<>();
        List<Pair> deltas = getBasicDeltas();
        for (Pair delta : deltas) {
            Cell shifted = initial.shift(delta.first(), delta.second());
            boolean legal = shifted.withinBounds() && position.canOccupy(this, shifted);
            if (!canJump()) {
                legal &= position.isFreePathBetween(initial, shifted);
            }
            if (legal) {
                moves.add(new Move(initial, shifted));
            }
        }
        return moves;
    }

    public List<Move> getAdditionalLegalMoves(final Cell initial, final Position position, final Move lastMove) {
        return new ArrayList<>();
    }

    public List<Move> getLegalMoves(final Cell initial, final Position position, final Move lastMove) {
        List<Move> basic = getBasicLegalMoves(initial, position);
        List<Move> moves = new ArrayList<>(basic);
        List<Move> additional = getAdditionalLegalMoves(initial, position, lastMove);
        moves.addAll(additional);
        return moves;
    }
}