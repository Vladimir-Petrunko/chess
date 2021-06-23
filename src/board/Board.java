package board;

import pieces.*;
import utils.FreePathChecker;
import utils.Pair;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.Global.CASTLING_DELTA;
import static utils.Global.SIZE;

public class Board {
    private static final Map<Character, Class<?>> charToPiece = Map.of(
            'P', Pawn.class,
            'N', Knight.class,
            'B', Bishop.class,
            'R', Rook.class,
            'Q', Queen.class,
            'K', King.class
    );

    public static class Position {
        private final Piece[][] grid = new Piece[SIZE][SIZE];
        private final boolean[][] hasMoved = new boolean[SIZE][SIZE];
        private final Cell[] kings = new Cell[2];
        private final List<Cell>[] pieces = new List[]{new ArrayList<>(), new ArrayList<>()};
        private final FreePathChecker checker = new FreePathChecker(SIZE);

        public Position(final String order) {
            Class<?>[] defaultOrder = new Class[SIZE];
            for (int i = 0; i < SIZE; i++) {
                char c = Character.toUpperCase(order.charAt(i));
                defaultOrder[i] = charToPiece.get(c);
            }
            for (int i = 0; i < SIZE; i++) {
                set(1, i, new Pawn(Color.BLACK));
                set(SIZE - 2, i, new Pawn(Color.WHITE));
            }
            for (int i = 0; i < SIZE; i++) {
                try {
                    Class<?> clazz = defaultOrder[i];
                    Constructor<?> constructor = clazz.getConstructor(Color.class);
                    Piece white = (Piece) constructor.newInstance(Color.WHITE);
                    Piece black = (Piece) constructor.newInstance(Color.BLACK);
                    set(0, i, black);
                    set(SIZE - 1, i, white);
                } catch (Exception ignored) {
                    // Everything okay, program should not enter this block
                }
            }
        }

        public Position(final Piece[][] grid) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    set(i, j, grid[i][j]);
                }
            }
        }

        public Piece get(final Cell cell) {
            return get(cell.getRow(), cell.getCol());
        }

        public Piece get(final int row, final int col) {
            return grid[row][col];
        }

        private void set(final Cell cell, final Piece piece) {
            set(cell.getRow(), cell.getCol(), piece);
        }

        public boolean isOccupied(final Cell cell) {
            return get(cell) != null;
        }

        public boolean hasMoved(final Cell cell) {
            return hasMoved[cell.getRow()][cell.getCol()];
        }

        public Cell getKingCell(final Color color) {
            return color == Color.WHITE ? kings[0] : kings[1];
        }

        private void set(final int row, final int col, final Piece piece) {
            grid[row][col] = piece;
            if (piece == null) {
                checker.remove(row, col);
                hasMoved[row][col] = false;
            } else {
                checker.set(row, col);
                hasMoved[row][col] = true;
                Cell cell = new Cell(row, col);
                int index = piece.getColor() == Color.WHITE ? 0 : 1;
                pieces[index].add(cell);
                if (piece instanceof King) {
                    kings[index] = cell;
                }
            }
        }

        public boolean adjacent(final Cell a, final Cell b) {
            int dr = b.getRow() - a.getRow();
            int dc = b.getCol() - a.getCol();
            return dr * dr + dc * dc == 1;
        }

        public boolean canOccupy(final Piece piece, final Cell cell) {
            Piece p = get(cell);
            return p == null || p.getColor() != piece.getColor();
        }

        public boolean isFreePathBetween(final Cell start, final Cell target) {
            return checker.isFreePathBetween(start, target);
        }

        public boolean isHorizontalRangeUnattacked(final Cell start, final Cell target) {
            int l = Math.min(start.getCol(), target.getCol());
            int r = Math.max(start.getCol(), target.getCol());
            int row = start.getRow();
            for (int col = l; col <= r; col++) {
                if (isAttacked(new Cell(row, col))) {
                    return false;
                }
            }
            return true;
        }

        public boolean attacks(final Cell start, final Cell target) {
            Piece piece = get(start);
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            boolean flag = piece.validAttackDelta(dr, dc);
            if (!piece.canJump()) {
                flag &= isFreePathBetween(start, target);
            }
            return flag;
        }

        public Pair totalAttackCount(final Cell cell) {
            int white = 0;
            int black = 0;
            for (Cell c : pieces[0]) {
                white += attacks(c, cell) ? 1 : 0;
            }
            for (Cell c : pieces[1]) {
                black += attacks(c, cell) ? 1 : 0;
            }
            return new Pair(white, black);
        }

        public boolean isAttacked(final Cell cell) {
            Pair pair = totalAttackCount(cell);
            Color color = get(cell).getColor();
            return color == Color.WHITE ? pair.second() > 0 : pair.first() > 0;
        }

        public boolean isKingInCheck(final Color color) {
            return isAttacked(color == Color.WHITE ? kings[0] : kings[1]);
        }

        public boolean isLegalMove(final Move move, final Move lastMove) {
            Cell start = move.getStart();
            Cell target = move.getTarget();
            Piece piece = get(start);
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            if (piece == null || (!piece.canJump() && !isFreePathBetween(start, target))) {
                return false;
            }
            List<Move> additionalMoves = piece.getAdditionalLegalMoves(start, this, lastMove);
            if (additionalMoves.contains(move)) {
                return true;
            }
            if (isOccupied(target)) {
                Color thisColor = piece.getColor();
                Color thatColor = get(target).getColor();
                return thisColor != thatColor && piece.validAttackDelta(dr, dc);
            } else {
                return piece.validMoveDelta(dr, dc);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    sb.append(grid[i][j] == null ? "." : grid[i][j].getSymbol()).append('\t');
                }
                sb.append((SIZE - i));
                sb.append('\n');
            }
            for (int i = 0; i < SIZE; i++) {
                sb.append((char)('a' + i)).append('\t');
            }
            return sb.toString();
        }
    }

    private final Position position;
    private Move lastMove = null;

    public Board(final Position position) {
        this.position = position;
    }

    private void shift(final Cell cell, final int dr, final int dc) {
        Piece piece = position.get(cell);
        Cell target = cell.shift(dr, dc);
        position.set(cell, null);
        position.set(target, piece);
    }

    public void makeMove(final Move move) {
        if (position.isLegalMove(move, lastMove)) {
            MoveCategory category = move.getCategory();
            if (category == MoveCategory.ORDINARY) {
                Cell start = move.getStart();
                Cell target = move.getTarget();
                Piece piece = position.get(start);
                position.set(start, null);
                position.set(target, piece);
                lastMove = move;
            } else if (category == MoveCategory.O_O || category == MoveCategory.O_O_O) {
                Cell king = move.getStart();
                Cell rook = move.getTarget();
                int multiplier = category == MoveCategory.O_O ? 1 : -1;
                shift(king, 0, CASTLING_DELTA * multiplier);
                shift(rook, 0, (rook.getCol() - king.getCol() - CASTLING_DELTA + 1) * multiplier);
            }
        }
    }

    @Override
    public String toString() {
        return position.toString();
    }
}