package board;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pieces.*;
import utils.FreePathChecker;
import utils.Pair;

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
        private Move lastMove = null;

        /**
         * Private method that initializes all elements of the {@code hasMoved} array to {@code false}.
         */
        private void initHasMoved() {
            for (int i = 0; i < SIZE; i++) {
                Arrays.fill(hasMoved[i], false);
            }
        }

        /**
         * Constructor of {@code Position} from a back rank order.<br><br>
         *
         * The back rank order should have length {@code SIZE} and consist of characters from the set {@code {"Q", "R",
         * "B", "N", "K"}} or their lowercase variants. This string represents the left-to-right order of pieces on the
         * back rank, as viewed in the traditional chessboard topology (black pieces on top, white pieces below).
         *
         * @param order the back rank order, as described above
         */
        public Position(String order) {
            Class<?>[] defaultOrder = new Class[SIZE];
            for (int i = 0; i < SIZE; i++) {
                char c = Character.toUpperCase(order.charAt(i));
                defaultOrder[i] = charToPiece.get(c);
            }
            // Pawns
            for (int i = 0; i < SIZE; i++) {
                set(1, i, new Pawn(Color.BLACK));
                set(SIZE - 2, i, new Pawn(Color.WHITE));
            }
            // Pieces
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
            initHasMoved(); // because we touched elements of hasMoved
        }

        /**
         * Constructor of {@code Position} from a custom matrix of pieces.
         *
         * @param grid a 2-dimensional array of pieces
         */
        public Position(Piece[][] grid) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    set(i, j, grid[i][j]);
                }
            }
            initHasMoved();
        }

        /**
         * Returns the piece located at the given cell.
         *
         * @param cell a {@code Cell}
         *
         * @return the piece located at {@code cell}
         * 
         * @see #get(int, int) get(int, int)
         */
        public Piece get(Cell cell) {
            return get(cell.getRow(), cell.getCol());
        }

        /**
         * Returns the piece located at the given cell.
         *
         * @param row the row index of a cell
         * @param col the column index of a cell
         *
         * @return the piece located at {@code cell}
         *
         * @see #get(Cell) get(Cell)
         */
        public Piece get(int row, int col) {
            return grid[row][col];
        }

        /**
         * Returns the color of a piece in a given cell.
         *
         * @param cell a {@code Cell}
         *
         * @return the color of the piece in {@code cell}, or {@code null} if there is no piece in {@code cell}
         */
        public Color getColor(Cell cell) {
            Piece piece = get(cell);
            return piece == null ? null : piece.getColor();
        }

        /**
         * Returns the leftmost column index, from the point of view of a particular color. (By default, the orientation
         * is standard: black pieces above, white pieces below).
         *
         * @param color a {@code color}
         *
         * @return the leftmost column index, from the point of view of {@code color}
         */
        public int getLeftmostColumn(Color color) {
            return color == Color.WHITE ? 0 : SIZE - 1;
        }

        /**
         * Returns the rightmost column index, from the point of view of a particular color. (By default, the orientation
         * is standard: black pieces above, white pieces below).
         *
         * @param color a {@code color}
         *
         * @return the rightmost column index, from the point of view of {@code color}
         */
        public int getRightmostColumn(Color color) {
            return color == Color.WHITE ? SIZE - 1 : 0;
        }

        /**
         * Private method that sets the contents of the given cell to a particular piece.<br><br>
         *
         * The method is private to enable encapsulation: it is impossible to directly alter the contents of a position
         * from outside, as {@code Position} is an inner class of {@code Board}.
         *
         * @param cell a {@code Cell}
         * @param piece a {@code Piece}
         *
         * @see #set(int, int, Piece) set(int, int, Piece)
         */
        private void set(Cell cell, Piece piece) {
            set(cell.getRow(), cell.getCol(), piece);
        }

        /**
         * Private method that sets the contents of the given cell to a particular piece.<br><br>
         *
         * This method is private to enable encapsulation: it is impossible to directly alter the contents of a position
         * from outside, as {@code Position} is an inner class of {@code Board}.
         *
         * @param row the row index of the cell
         * @param col the column index of the cell
         * @param piece a {@code Piece}
         *
         * @see #set(Cell, Piece) set(Cell, Piece)
         */
        private void set(int row, int col, Piece piece) {
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

        /**
         * Determines whether the given cell is occupied (i.e. contains a piece).
         *
         * @param cell a {@code Cell}
         *
         * @return {@code true} if {@code cell} is occupied, {@code false} otherwise
         */
        public boolean isOccupied(Cell cell) {
            return get(cell) != null;
        }

        /**
         * Determines whether the piece located at the given cell has moved before.<br><br>
         *
         * This method is useful, for example, when checking the legality of castling: by the rules of chess, one of the
         * necessary preconditions of a castling move is that both the king and the rook in question must not have moved
         * before.
         *
         * @param cell a {@code Cell}
         *
         * @return {@code true} if the piece located at {@code cell} has moved before, {@code false} otherwise
         */
        public boolean hasMoved(Cell cell) {
            return hasMoved[cell.getRow()][cell.getCol()];
        }

        /**
         * Returns the cell currently containing the king of the given color.
         *
         * @param color a {@code Color}
         *
         * @return the cell currently containing the king of the color {@code color}
         */
        public Cell getKingCell(Color color) {
            return color == Color.WHITE ? kings[0] : kings[1];
        }

        /**
         * Determines whether a piece passed as a parameter could (theoretically) occupy a given cell.<br><br>
         *
         * This can happen in two cases: either the cell is empty, or the cell contains a piece of the opposite color
         * (in this case a capture would take place).
         *
         * @param piece a {@code Piece}
         * @param cell a {@code Cell}
         *
         * @return {@code true} if {@code piece} could occupy {@code cell}, {@code false} otherwise
         */
        public boolean canOccupy(Piece piece, Cell cell) {
            Piece p = get(cell);
            return p == null || p.getColor() != piece.getColor();
        }

        /**
         * Determines whether there is a free (that is, not consisting of pieces of any color) horizontal, vertical or
         * diagonal path between two cells passed as parameters. Both endpoints are exclusive, i.e. only the path
         * strictly between the two cells matters.
         *
         * @param first a {@code Cell}
         * @param second a {@code Cell}
         *
         * @return {@code true} if there is a free path (as described above) between {@code first} and {@code second},
         * {@code false} otherwise
         */
        public boolean isFreePathBetween(Cell first, Cell second) {
            return checker.isFreePathBetween(first, second);
        }

        /**
         * Determines whether there is a free (that is, not consisting of pieces of any color <b>or attacked cells</b>)
         * horizontal path between two cells in the same row passed as parameters. Both endpoints are exclusive, i.e.
         * only the path strictly between the two cells matters.<br><br>
         *
         * Note that the logic of this method is slightly different from the logic of {@link #isFreePathBetween(Cell,
         * Cell)} due to the additional restriction that the intermediate cells should not be attacked.
         *
         * @param first a {@code Cell}
         * @param second a {@code Cell}
         * @param color a {@code Color}
         *
         * @return {@code true} if there is a free path (as described above) between {@code first} and {@code second},
         * {@code false} otherwise
         */
        public boolean isHorizontalRangeUnattacked(Cell first, Cell second, Color color) {
            int l = Math.min(first.getCol(), second.getCol());
            int r = Math.max(first.getCol(), second.getCol());
            int row = first.getRow();
            for (int col = l; col <= r; col++) {
                if (isAttacked(new Cell(row, col), color)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Determines whether a given cell has a piece that attacks another given cell.<br><br>
         *
         * If the initial cell does not contain a piece, then the method returns {@code false}. However the target cell
         * <i>may</i> be empty.
         *
         * @param start a {@code Cell}
         * @param target a {@code Cell}
         *
         * @return {@code true} if the piece located at {@code start} attacks {@code target}, {@code false} otherwise
         */
        public boolean attacks(Cell start, Cell target) {
            Piece piece = get(start);
            if (piece == null) {
                return false;
            }
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            boolean flag = piece.validCaptureDelta(dr, dc);
            if (!piece.canJump()) {
                flag &= isFreePathBetween(start, target);
            }
            return flag;
        }

        /**
         * Counts the number of white and black pieces (separately) that attack a given cell and returns the result as
         * a pair.
         *
         * @param cell a {@code Cell}
         *
         * @return a pair consisting of the number of "white" and "black" attacks on {@code cell}
         */
        public Pair totalAttackCount(Cell cell) {
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

        /**
         * Determines whether a given cell is attacked by any piece of a particular color.
         *
         * @param cell a {@code Cell}
         * @param color a {@code Color}
         *
         * @return {@code true} if there exists a piece of color {@code color} that attacks {@code cell}, {@code false}
         * otherwise.
         */
        public boolean isAttacked(Cell cell, Color color) {
            Pair pair = totalAttackCount(cell);
            return color == Color.WHITE ? pair.first() > 0 : pair.second() > 0;
        }

        /**
         * Determines whether a move is legal. Also takes as parameter the last move in order to check the legality of
         * a possible en passant capture.
         *
         * @param move a {@code Move}
         * @param lastMove a {@code Move}
         *
         * @return {@code true} if {@code move} is legal, {@code false} otherwise
         */
        public boolean isLegalMove(Move move, Move lastMove) {
            Cell start = move.getStart();
            Cell target = move.getTarget();
            Piece piece = get(start);
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            if (piece == null || (!piece.canJump() && !isFreePathBetween(start, target))) {
                // Either start cell is empty or path to destination cell is blocked (and piece cannot jump)
                return false;
            }
            List<Move> additionalMoves = piece.getAdditionalLegalMoves(start, this, lastMove);
            if (additionalMoves.contains(move)) {
                return true;
            }
            if (isOccupied(target)) {
                // Check for valid capture
                Color thisColor = piece.getColor();
                Color thatColor = get(target).getColor();
                return thisColor != thatColor && piece.validCaptureDelta(dr, dc);
            } else {
                // Check for valid non-capture
                return piece.validMoveDelta(dr, dc);
            }
        }

        /**
         * Determines whether a move is legal.<br><br>
         *
         * This one-parameter method is needed because {@code lastMove} is private, so this is the method expected to
         * be called from the outside.
         *
         * @param move {@code Move}
         * 
         * @return {@code true} if {@code move} is legal, {@code false} otherwise
         */
        public boolean isLegalMove(Move move) {
            return isLegalMove(move, lastMove);
        }

        /**
         * @return a grid representation of the current position
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    sb.append(grid[i][j] == null ? "." : grid[i][j].getSymbol()).append('\t');
                }
                sb.append((SIZE - i)); // Row indices
                sb.append('\n');
            }
            for (int i = 0; i < SIZE; i++) {
                sb.append((char)('a' + i)).append('\t'); // Column indices
            }
            return sb.toString();
        }
    }

    private final Position position;

    public Board(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * Private method that shifts the piece located in a particular cell. Helps avoid code duplication when handling
     * castling.
     *
     * @param cell a {@code Cell}
     * @param dr the row displacement
     * @param dc the column displacement
     */
    private void shift(Cell cell, int dr, int dc) {
        Piece piece = position.get(cell);
        Cell target = cell.shift(dr, dc);
        position.set(cell, null);
        position.set(target, piece);
    }

    /**
     * Makes a move in the current position. If the move is invalid, leaves the position as is.
     *
     * @param move a {@code Move}
     *
     * @return {@code true} if {@code move} was valid, {@code false} otherwise
     */
    public boolean makeMove(Move move) {
        if (position.isLegalMove(move)) {
            MoveCategory category = move.getCategory();
            if (category == MoveCategory.ORDINARY) {
                Cell start = move.getStart();
                Cell target = move.getTarget();
                Piece piece = position.get(start);
                position.set(start, null);
                position.set(target, piece);
                position.lastMove = move;
            } else if (category == MoveCategory.O_O || category == MoveCategory.O_O_O) {
                Cell king = move.getStart();
                Cell rook = move.getTarget();
                int multiplier = category == MoveCategory.O_O ? 1 : -1;
                shift(king, 0, CASTLING_DELTA * multiplier);
                shift(rook, 0, (rook.getCol() - king.getCol() - CASTLING_DELTA + 1) * -multiplier);
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return position.toString();
    }


}