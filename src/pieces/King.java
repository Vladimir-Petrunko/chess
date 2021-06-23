package pieces;

import java.util.List;
import java.util.ArrayList;
import board.Board.Position;
import board.Color;
import board.Cell;
import board.Move;
import board.MoveCategory;
import utils.Pair;

import static utils.Global.SIZE;
import static utils.Global.CASTLING_DELTA;

public class King extends Piece {
    private static final List<Pair> deltas = new ArrayList<Pair>();

    static {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (!(dr == 0 && dc == 0)) {
                    deltas.add(new Pair(dr, dc));
                }
            }
        }
    }

    public King(final Color color) {
        super(color);
    }

    @Override
    public List<Pair> getBasicDeltas() {
        return deltas;
    }

    @Override
    public boolean validMoveDelta(final int dr, final int dc) {
        return Math.abs(dr) <= 1 && Math.abs(dc) <= 1 && !(dr == 0 && dc == 0);
    }

    private Move checkCastling(final Position position, final Cell kingCell, final Cell rookCell, final MoveCategory category) {
        if (position.hasMoved(kingCell) || position.hasMoved(rookCell) || !position.isHorizontalRangeUnattacked(kingCell, rookCell)) {
            return null;
        }
        int delta = (category == MoveCategory.O_O ? CASTLING_DELTA : -CASTLING_DELTA);
        Cell landingCell = kingCell.shift(0, delta);
        if (position.isOccupied(landingCell)) {
            return null;
        }
        return new Move(kingCell, rookCell, category);
    }

    @Override
    public ArrayList<Move> getAdditionalLegalMoves(final Cell initial, final Position position, final Move lastMove) {
        Cell kingCell = position.getKingCell(color);
        if (position.hasMoved(kingCell)) {
            return new ArrayList<>();
        } else {
            ArrayList<Move> list = new ArrayList<>();
            Move kingside = checkCastling(position, kingCell, new Cell(kingCell.getRow(), 0), MoveCategory.O_O);
            Move queenside = checkCastling(position, kingCell, new Cell(kingCell.getRow(), SIZE - 1), MoveCategory.O_O_O);
            if (kingside != null) {
                list.add(kingside);
            }
            if (queenside != null) {
                list.add(queenside);
            }
            return list;
        }
    }
}
