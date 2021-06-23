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
    private final int dir, nextPromotion;

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
    public boolean validMoveDelta(final int dr, final int dc) {
        return dr == dir && dc == 0;
    }

    @Override
    public boolean validAttackDelta(final int dr, final int dc) {
        return dr == dir && Math.abs(dc) == 1;
    }

    private List<Move> checkPromotion(final boolean condition, final Cell initial, final Cell target) {
        if (condition) {
            List<Move> list = new ArrayList<>();
            list.add(new Move(initial, target, MoveCategory.PROMOTE_TO_QUEEN));
            list.add(new Move(initial, target, MoveCategory.PROMOTE_TO_ROOK));
            list.add(new Move(initial, target, MoveCategory.PROMOTE_TO_BISHOP));
            list.add(new Move(initial, target, MoveCategory.PROMOTE_TO_KNIGHT));
            return list;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Move> getAdditionalLegalMoves(final Cell initial, final Position position, final Move lastMove) {
        ArrayList<Move> list = new ArrayList<>();
        // Double advance
        int initRow = SIZE - nextPromotion - 1;
        if (initial.getRow() == initRow) {
            Cell landingCell = initial.shift(dir * 2, 0);
            if (position.canOccupy(this, landingCell) && position.isFreePathBetween(initial, landingCell)) {
                list.add(new Move(initial, landingCell));
            }
        }
        if (lastMove == null) {
            return list;
        }
        // Pawn promotion
        if (initial.getRow() == nextPromotion) {
            Cell a = initial.shift(dir, -1);
            Cell b = initial.shift(dir, 0);
            Cell c = initial.shift(dir, 1);
            List<Move> left = checkPromotion(position.isOccupied(a), initial, a);
            List<Move> straight = checkPromotion(!position.isOccupied(b), initial, b);
            List<Move> right = checkPromotion(position.isOccupied(c), initial, c);
            if (left != null) {
                list.addAll(left);
            }
            if (straight != null) {
                list.addAll(straight);
            }
            if (right != null) {
                list.addAll(right);
            }
        }
        // En passant
        Cell lastStart = lastMove.getStart();
        Cell lastTarget = lastMove.getTarget();
        int dr = lastTarget.getRow() - lastStart.getRow();
        int dc = lastTarget.getCol() - lastStart.getCol();
        if (position.adjacent(initial, lastTarget) && Math.abs(dr) == 2 && dc == 0) {
            int offset = lastStart.getCol() - initial.getCol();
            Cell landingCell = initial.shift(dir, offset);
            list.add(new Move(initial, landingCell));
        }
        return list;
    }
}
