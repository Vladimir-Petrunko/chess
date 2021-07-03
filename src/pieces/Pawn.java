package pieces;

import java.util.HashSet;
import board.Board.Position;
import board.Cell;
import board.Color;
import board.Move;
import board.MoveCategory;
import utils.Pair;

import static utils.Global.SIZE;

public class Pawn extends Piece {
    private static final HashSet<Pair> whiteDeltas = new HashSet<>();
    private static final HashSet<Pair> blackDeltas = new HashSet<>();

    static {
        whiteDeltas.add(new Pair(-1, 0));
        whiteDeltas.add(new Pair(-1, -1));
        whiteDeltas.add(new Pair(-1, 1));
        blackDeltas.add(new Pair(1, 0));
        blackDeltas.add(new Pair(1, -1));
        blackDeltas.add(new Pair(1, 1));
    }

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
    public HashSet<Pair> getBasicDeltas() {
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
    private HashSet<Move> checkPromotion(boolean condition, Cell initial, Cell target) {
        if (condition) {
            HashSet<Move> set = new HashSet<>();
            set.add(new Move(initial, target, this, MoveCategory.PROMOTE_TO_QUEEN));
            set.add(new Move(initial, target, this, MoveCategory.PROMOTE_TO_BISHOP));
            set.add(new Move(initial, target, this, MoveCategory.PROMOTE_TO_KNIGHT));
            set.add(new Move(initial, target, this, MoveCategory.PROMOTE_TO_ROOK));
            return set;
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public HashSet<Move> getAdditionalLegalMoves(Cell initial, Position position, Move lastMove) {
        HashSet<Move> list = new HashSet<>();
        // Double advance
        int initRow = SIZE - nextPromotion - 1;
        if (initial.getRow() == initRow) {
            // Pawn hasn't moved yet
            Cell landingCell = initial.shift(dir * 2, 0);
            if (!position.isOccupied(landingCell) && position.isFreePathBetween(initial, landingCell)) {
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
            HashSet<Move> left = checkPromotion(position.isOccupied(a), initial, a);
            HashSet<Move> straight = checkPromotion(!position.isOccupied(b), initial, b);
            HashSet<Move> right = checkPromotion(position.isOccupied(c), initial, c);
            list.addAll(left);
            list.addAll(straight);
            list.addAll(right);
        }
        // En passant
        Cell lastStart = lastMove.getStart();
        Cell lastTarget = lastMove.getTarget();
        int dr = lastTarget.getRow() - lastStart.getRow();
        if (initial.isBeside(lastTarget) && lastMove.getPiece() instanceof Pawn && Math.abs(dr) == 2) {
            int offset = lastStart.getCol() - initial.getCol();
            Cell landingCell = initial.shift(dir, offset);
            list.add(new Move(initial, landingCell, this));
        }
        HashSet<Move> legal = new HashSet<>();
        for (Move move : list) {
            if (position.isKingSafeMove(move)) {
                legal.add(move);
            }
        }
        return legal;
    }
}
