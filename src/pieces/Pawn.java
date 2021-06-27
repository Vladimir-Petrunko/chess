package pieces;

import java.util.List;
import java.util.ArrayList;
import board.Board.Position;
import board.Cell;
import board.Color;
import board.Move;
import board.MoveCategory;
import utils.Pair;

import static utils.Global.SIZE;

public class Pawn extends Piece {
    private static final List<Pair> whiteDeltas = List.of(new Pair(1, 0));
    private static final List<Pair> blackDeltas = List.of(new Pair(-1, 0));

    private final int dir; // direction of motion: -1 (to decreasing row indices) or 1 (to increasing row indices)
    private final int nextPromotion; // index of row just preceding promotion row

    /**
     * Constructor of {@code Pawn} of a given color.
     *
     * @param color the color of this pawn
     */
    public Pawn(Color color) {
        super(color);
        dir = (color == Color.WHITE ? -1 : 1);
        nextPromotion = (color == Color.WHITE ? 1 : SIZE - 2);
    }

    @Override
    public List<Pair> getBasicDeltas() {
        return color == Color.WHITE ? whiteDeltas : blackDeltas;
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return dr == dir && dc == 0;
    }

    @Override
    public boolean validCaptureDelta(int dr, int dc) {
        return dr == dir && Math.abs(dc) == 1;
    }

    /**
     * Private method that returns a list of all possible promotion moves of this pawn with a specified source and
     * destination cell, provided some initial condition is true.<br><br>
     *
     * The condition can be anything, as the method is purely auxiliary and serves to avoid code duplication.
     * Ideologically, the condition should represent the legality of this particular pawn promotion.
     *
     * @param condition a condition that should evaluate to {@code true}
     * @param initial the initial cell of the pawn
     * @param target the destination cell of the pawn
     *
     * @return a list of all possible promotion moves of this pawn from {@code initial} to {@code target}
     */
    private List<Move> checkPromotion(boolean condition, Cell initial, Cell target) {
        if (condition) {
            return List.of(
                new Move(initial, target, null, MoveCategory.PROMOTE_TO_QUEEN),
                new Move(initial, target, null, MoveCategory.PROMOTE_TO_ROOK),
                new Move(initial, target, null, MoveCategory.PROMOTE_TO_BISHOP),
                new Move(initial, target, null, MoveCategory.PROMOTE_TO_KNIGHT)
            );
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Move> getAdditionalLegalMoves(Cell initial, Position position, Move lastMove) {
        List<Move> list = new ArrayList<>();
        // Double advance
        int initRow = SIZE - nextPromotion - 1;
        if (initial.getRow() == initRow) {
            // Pawn hasn't moved yet
            Cell landingCell = initial.shift(dir * 2, 0);
            if (position.canOccupy(this, landingCell) && position.isFreePathBetween(initial, landingCell)) {
                list.add(new Move(initial, landingCell, position.get(initial)));
            }
        }
        if (lastMove == null) {
            // No moves have been made in this game => other additional moves are definitely impossible
            return list;
        }
        // Pawn promotion
        if (initial.getRow() == nextPromotion) {
            Cell a = initial.shift(dir, -1);
            Cell b = initial.shift(dir, 0);
            Cell c = initial.shift(dir, 1);
            // If the pawn changes its column, then it must capture, otherwise it must not capture
            List<Move> left = checkPromotion(position.isOccupied(a), initial, a);
            List<Move> straight = checkPromotion(!position.isOccupied(b), initial, b);
            List<Move> right = checkPromotion(position.isOccupied(c), initial, c);
            list.addAll(left);
            list.addAll(straight);
            list.addAll(right);
        }
        // En passant
        Cell lastStart = lastMove.getStart();
        Cell lastTarget = lastMove.getTarget();
        int dr = lastTarget.getRow() - lastStart.getRow();
        int dc = lastTarget.getCol() - lastStart.getCol();
        if (position.adjacent(initial, lastTarget) && Math.abs(dr) == 2 && dc == 0) {
            int offset = lastStart.getCol() - initial.getCol();
            Cell landingCell = initial.shift(dir, offset);
            list.add(new Move(initial, landingCell, position.get(initial)));
        }
        return list;
    }
}
