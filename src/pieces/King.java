package pieces;

import java.util.HashSet;
import board.Board.Position;
import board.Color;
import board.Cell;
import board.Move;
import board.MoveCategory;
import utils.Pair;

import static utils.Global.CASTLING_DELTA;
import static utils.Global.SIZE;

public class King extends Piece {
    private static final HashSet<Pair> deltas = new HashSet<Pair>();

    static {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (!(dr == 0 && dc == 0)) {
                    deltas.add(new Pair(dr, dc));
                }
            }
        }
    }

    public King(Color color) {
        super(color);
    }

    @Override
    public HashSet<Pair> getBasicDeltas() {
        return deltas;
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return Math.abs(dr) <= 1 && Math.abs(dc) <= 1 && !(dr == 0 && dc == 0);
    }

    /**
     * Private method that checks a potential castling move for legality. If castling is legal, generates and returns
     * the corresponding move.<br><br>
     *
     * The parameter {@code kingCell} (and also {@code rookCell}) is unnecessary, since {@code Position} has the method
     * {@link board.Board.Position#getKingCell(Color) Position.getKingCell(Color)}, however
     *
     * @param position a {@code Position}
     * @param kingCell the current cell of this king in {@code position}
     * @param rookCell the current cell of one of the rooks in {@code position}
     * @param category the type of castling, either {@code MoveCategory.O_O} or {@code MoveCategory.O_O_O}
     *
     * @return a castling {@code Move} if such a move is legal, otherwise {@code null}
     */
    private Move checkCastling(Position position, Cell kingCell, Cell rookCell, MoveCategory category) {
        Color color = position.get(kingCell).getColor();
        int delta = (category == MoveCategory.O_O ? CASTLING_DELTA : -CASTLING_DELTA);
        // TODO: may not work with Fischer random chess, depending on the initial board layout
        Cell landingCell = kingCell.shift(0, delta);
        if (!position.hasMoved(kingCell) &&
            !position.isKingInCheck(color) &&
            !position.hasMoved(rookCell) &&
            position.get(rookCell) instanceof Rook &&
            position.getColor(rookCell) == position.getColor(kingCell) &&
            // Last two conditions - in case a Position was created separately
            position.isHorizontalRangeUnattacked(kingCell, landingCell, color)) {
            // All necessary conditions for castling are satisfied
            return new Move(kingCell, landingCell, this);
        } else {
            // This castling move is illegal
            return null;
        }
    }

    @Override
    public HashSet<Move> getAdditionalLegalMoves(Cell initial, Position position, Move lastMove) {
        HashSet<Move> list = new HashSet<>();
        int row = initial.getRow();
        Move kingside = checkCastling(position, initial, new Cell(row, SIZE - 1), MoveCategory.O_O);
        Move queenside = checkCastling(position, initial, new Cell(row, 0), MoveCategory.O_O_O);
        if (kingside != null) list.add(kingside);
        if (queenside != null) list.add(queenside);
        return list;
    }
}
