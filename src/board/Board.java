package board;

import java.lang.reflect.Constructor;
import java.util.*;
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
        private final int[][] movesMade = new int[SIZE][SIZE];
        private final Cell[] kings = new Cell[2];
        private final HashSet<Cell>[] pieces = new HashSet[]{new HashSet<>(), new HashSet<>()};
        private final FreePathChecker checker = new FreePathChecker(SIZE);
        private Move lastMove = null;

        /**
         * Private method that initializes all elements of the {@code movesMade} array to {@code 0}. <br><br>
         *
         * {@code movesMade[i][j]} represents the number of times that the piece currently located at {@code (i, j)}
         * moved.
         */
        private void initHasMoved() {
            for (int i = 0; i < SIZE; i++) {
                Arrays.fill(movesMade[i], 0);
            }
        }

        // TODO: generalize to sizes not equal to 8
        /**
         * Initializes a new {@code Position} from a default back rank ordering. Currently not adapted to sizes other
         * than the default value of 8.
         */
        public Position() {
            this("RNBQKBNR");
        }

        /**
         * Initializes a new {@code Position} from a provided back rank ordering.
         *
         * The back rank order should have length {@code SIZE} and consist of characters from the set {@code {"Q", "R",
         * "B", "N", "K"}} or their lowercase variants. This string represents the left-to-right order of pieces on the
         * back rank, as viewed in the traditional chessboard orientation (black pieces above, white pieces below).
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
                set(1, i, new Pawn(Color.BLACK), 0);
                set(SIZE - 2, i, new Pawn(Color.WHITE), 0);
            }
            // Pieces
            for (int i = 0; i < SIZE; i++) {
                try {
                    Class<?> clazz = defaultOrder[i];
                    Constructor<?> constructor = clazz.getConstructor(Color.class);
                    Piece white = (Piece) constructor.newInstance(Color.WHITE);
                    Piece black = (Piece) constructor.newInstance(Color.BLACK);
                    set(0, i, black, 0);
                    set(SIZE - 1, i, white, 0);
                } catch (Exception ignored) {
                    // Everything okay, program should not enter this block
                }
            }
            initHasMoved(); // because we touched elements of hasMoved
        }

        /**
         * Initializes a new {@code Position} from a custom piece arrangement
         *
         * @param grid a 2-dimensional array of pieces
         */
        public Position(Piece[][] grid) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    set(i, j, grid[i][j], 0);
                }
            }
            initHasMoved(); // because we touched elements of hasMoved
        }

        /**
         * Returns the piece located at the given cell.
         *
         * @param cell a {@code Cell}
         * @return the piece located at {@code cell}
         * @see #get(int, int)
         */
        public Piece get(Cell cell) {
            return get(cell.getRow(), cell.getCol());
        }

        /**
         * Returns the piece located at the given cell.
         *
         * @param row the row index of the cell
         * @param col the column index of the cell
         * @return the piece located at {@code (row, col)}
         * @see #get(Cell)
         */
        public Piece get(int row, int col) {
            return grid[row][col];
        }

        /**
         * Returns the color of a piece in a given cell.
         *
         * @param cell a {@code Cell}
         * @return the color of the piece in {@code cell}, or {@code null} if there is no piece in {@code cell}
         */
        public Color getColor(Cell cell) {
            Piece piece = get(cell);
            return piece == null ? null : piece.getColor();
        }

        /**
         * Private method that sets the contents of the given cell to a particular piece.<br><br>
         *
         * This method is private to enable encapsulation: it is impossible to directly alter the contents of a position
         * from outside, as {@code Position} is an inner class of {@code Board}.
         *
         * @param cell a {@code Cell}
         * @param piece a {@code Piece}
         * @param pieceMoveCount the number of moves made by {@code piece} before
         * @see #set(int, int, Piece, int)
         */
        private void set(Cell cell, Piece piece, int pieceMoveCount) {
            set(cell.getRow(), cell.getCol(), piece, pieceMoveCount);
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
         * @param pieceMoveCount the number of moves made by {@code piece} before
         * @see #set(Cell, Piece, int)
         */
        private void set(int row, int col, Piece piece, int pieceMoveCount) {
            if (piece == null) {
                clear(row, col);
            } else {
                grid[row][col] = piece;
                checker.set(row, col);
                movesMade[row][col] = pieceMoveCount + 1;
                Cell cell = new Cell(row, col);
                int index = piece.getColor() == Color.WHITE ? 0 : 1;
                pieces[index].add(cell);
                if (piece instanceof King) {
                    kings[index] = cell;
                }
            }
        }

        /**
         * Private method that clears the contents of a given cell.
         *
         * @param row the row index of the cell
         * @param col the column index of the cell
         * @see #clear(Cell)
         */
        private void clear(int row, int col) {
            Piece piece = grid[row][col];
            if (piece != null) {
                int index = piece.getColor() == Color.WHITE ? 0 : 1;
                pieces[index].remove(new Cell(row, col));
            }
            grid[row][col] = null;
            checker.remove(row, col);
            movesMade[row][col] = 0;
        }

        /**
         * Private method that clears the contents of a given cell.
         *
         * @param cell a {@code Cell}
         * @see #clear(int, int)
         */
        private void clear(Cell cell) {
            clear(cell.getRow(), cell.getCol());
        }

        /**
         * Private method that moves a piece from its initial cell to a target cell.<br><br>
         *
         * This method should be only called when the source cell contains a piece.
         *
         * @param start the initial cell of the piece
         * @param target the target cell of the piece
         * @see #unmove(Cell, Cell)
         */
        private void move(Cell start, Cell target) {
            int cnt = movesMade(start);
            Piece piece = get(start);
            clear(start);
            set(target, piece, cnt);
        }

        /**
         * Private method that cancels a move from a source cell to a target cell.<br><br>
         *
         * Since move counts are affected in a non-obvious way, this method should be called after a corresponding call
         * to {@link #move(Cell, Cell)}.
         *
         * @param start the former initial cell of the piece
         * @param target the former target cell of the piece
         * @see #move(Cell, Cell)
         */
        private void unmove(Cell start, Cell target) {
            int cnt = movesMade(target);
            Piece piece = get(target);
            clear(target);
            set(start, piece, cnt - 2);
        }

        /**
         * Determines whether the given cell is occupied (i.e. contains a piece).
         *
         * @param cell a {@code Cell}
         * @return {@code true} if {@code cell} is occupied, or {@code false} otherwise
         */
        public boolean isOccupied(Cell cell) {
            return get(cell) != null;
        }

        /**
         * Determines whether the piece located at the given cell has moved before.<br><br>
         *
         * This method is useful, for example, when checking the legality of castling: by the rules of chess, one of the
         * necessary preconditions of a castling move is that both the king and the rook in question must not have moved
         * before. There may be other uses as well.
         *
         * @param cell a {@code Cell}
         * @return {@code true} if the piece located at {@code cell} has moved before, or {@code false} otherwise
         */
        public boolean hasMoved(Cell cell) {
            return movesMade[cell.getRow()][cell.getCol()] > 0;
        }

        /**
         * Determines the number of moves made by a piece located at the given cell.
         *
         * @param cell a {@code Cell}
         * @return the number of moves made by the piece located at {@code cell}
         */
        public int movesMade(Cell cell) {
            return movesMade[cell.getRow()][cell.getCol()];
        }

        /**
         * Returns the cell currently containing the king of the given color.
         *
         * @param color a {@code Color}
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
         * @return {@code true} if {@code piece} could occupy {@code cell}, or {@code false} otherwise
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
         * @param first one endpoint
         * @param second the other endpoint
         * @return {@code true} if there is a free path between {@code first} and {@code second}, exclusive, or {@code
         * false} otherwise
         */
        public boolean isFreePathBetween(Cell first, Cell second) {
            return checker.isFreePathBetween(first, second);
        }

        /**
         * Determines whether there is a free (that is, not consisting of pieces of any color <b>or attacked cells</b>)
         * horizontal path between two cells in the same row passed as parameters. Both endpoints are <b>exclusive, i.e.
         * only the path strictly between the two cells matters</b>. <br><br>
         *
         * Note that the logic of this method is slightly different from the logic of {@link #isFreePathBetween(Cell,
         * Cell)}.
         *
         * @param first one endpoint
         * @param second the other endpoint
         * @param color the color of pieces that must not attack the path
         * @return {@code true} if there is a free path (as described above) between {@code first} and {@code second},
         * exclusive, {@code false} otherwise
         */
        public boolean isHorizontalRangeUnattacked(Cell first, Cell second, Color color) {
            int l = Math.min(first.getCol(), second.getCol());
            int r = Math.max(first.getCol(), second.getCol());
            int row = first.getRow();
            for (int col = l + 1; col < r; col++) {
                if (isAttacked(new Cell(row, col), Color.getOppositeColor(color))) {
                    return false;
                }
            }
            return isFreePathBetween(first, second);
        }

        /**
         * Determines whether a given cell has a piece that attacks another given cell.<br><br>
         *
         * If the initial cell does not contain a piece, then the method returns {@code false}.
         *
         * @param start a {@code Cell}
         * @param target a {@code Cell}
         * @return {@code true} if the piece located at {@code start} attacks {@code target}, or {@code false} otherwise
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
         * @return a pair consisting of the number of "white" and "black" attacks on {@code cell}
         */
        public Pair totalAttackCount(Cell cell) {
            int white = 0;
            int black = 0;
            for (Cell c : pieces[0]) white += attacks(c, cell) ? 1 : 0;
            for (Cell c : pieces[1]) black += attacks(c, cell) ? 1 : 0;
            return new Pair(white, black);
        }

        /**
         * Determines whether a given cell is attacked by any piece of a given color.
         *
         * @param cell a {@code Cell}
         * @param color a {@code Color}
         * @return {@code true} if there exists a piece of color {@code color} that attacks {@code cell}, or {@code
         * false} otherwise.
         */
        public boolean isAttacked(Cell cell, Color color) {
            Pair pair = totalAttackCount(cell);
            return color == Color.WHITE ? pair.first() > 0 : pair.second() > 0;
        }

        /**
         * Determines whether the king of a given color is in check (i.e. is attacked).
         *
         * @param color a {@code Color}
         * @return {@code true} if the king of color {@code Color} is attacked, or {@code false} otherwise
         */
        public boolean isKingInCheck(Color color) {
            return isAttacked(getKingCell(color), Color.getOppositeColor(color));
        }

        /**
         * Determines whether a move is legal. Also takes as parameter the last move in order to check the legality of
         * a possible en passant capture.
         *
         * @param move the move whose legality is to be checked
         * @param lastMove the last move in the position
         * @return {@code true} if {@code move} is legal, or {@code false} otherwise
         */
        public boolean isLegalMove(Move move, Move lastMove) {
            Cell start = move.getStart();
            Cell target = move.getTarget();
            Piece piece = get(start);
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            boolean verdict;
            if (piece == null || (!piece.canJump() && !isFreePathBetween(start, target))) {
                // Either start cell is empty or path to destination cell is blocked (and piece cannot jump)
                verdict = false;
            } else {
                List<Move> additionalMoves = piece.getAdditionalLegalMoves(start, this, lastMove);
                if (additionalMoves.contains(move)) {
                    verdict = true;
                } else if (isOccupied(target)) {
                    // Check for valid capture
                    verdict = canOccupy(piece, target) && piece.validCaptureDelta(dr, dc);
                } else {
                    // Check for valid non-capture
                    verdict = piece.validMoveDelta(dr, dc);
                }
            }
            if (verdict) {
                Piece targetPiece = get(target);
                int targetCnt = movesMade(target);
                Color color = getColor(start);
                // Pretend we made the move and check whether our king becomes in check, then revert the move
                move(start, target);
                if (isKingInCheck(color)) {
                    verdict = false;
                }
                unmove(start, target);
                if (targetPiece != null) {
                    set(target, targetPiece, targetCnt - 1);
                }
            }
            return verdict;
        }

        /**
         * Determines whether a move is legal.<br><br>
         *
         * This one-parameter method is needed because {@code lastMove} is private, so this is the method expected to
         * be called from the outside.
         *
         * @param move the move whose legality is to be checked
         * @return {@code true} if {@code move} is legal, or {@code false} otherwise
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

    /**
     * Default constructor of {@code Board}
     *
     * @param position a {@code Position}
     */
    public Board(Position position) {
        this.position = position;
    }

    /**
     * @return the position associated with this board
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Makes a move in the current position. If the move is invalid, leaves the position as is.
     *
     * @param move a {@code Move}
     * @return {@code true} if {@code move} was valid, or {@code false} otherwise
     */
    public boolean makeMove(Move move) {
        if (position.isLegalMove(move)) {
            MoveCategory category = move.getCategory();
            if (category == MoveCategory.ORDINARY) {
                Cell start = move.getStart();
                Cell target = move.getTarget();
                position.move(start, target);
                position.lastMove = move;
            } else if (category == MoveCategory.O_O || category == MoveCategory.O_O_O) {
                Cell king = move.getStart();
                int rookColumn = (category == MoveCategory.O_O ? SIZE - 1 : 0);
                Cell rook = new Cell(king.getRow(), rookColumn);
                int multiplier = category == MoveCategory.O_O ? 1 : -1;
                position.move(king, king.shift(0, CASTLING_DELTA * multiplier));
                position.move(rook, king.shift(0, multiplier));
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