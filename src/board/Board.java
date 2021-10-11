package board;

import java.lang.reflect.Constructor;
import java.util.*;
import pieces.*;
import utils.Pair;

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
                set(new Cell(1, i), new Pawn(Color.BLACK), 0);
                set(new Cell(SIZE - 2, i), new Pawn(Color.WHITE), 0);
            }
            // Pieces
            for (int i = 0; i < SIZE; i++) {
                try {
                    Class<?> clazz = defaultOrder[i];
                    Constructor<?> constructor = clazz.getConstructor(Color.class);
                    Piece white = (Piece) constructor.newInstance(Color.WHITE);
                    Piece black = (Piece) constructor.newInstance(Color.BLACK);
                    set(new Cell(0, i), black, 0);
                    set(new Cell(SIZE - 1, i), white, 0);
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
                    set(new Cell(i, j), grid[i][j], 0);
                }
            }
            initHasMoved(); // because we touched elements of hasMoved
        }

        /**
         * Initializes a new {@code Position} from a custom piece arrangement and castling/last-move info
         *
         * @param grid a 2-dimensional array of pieces
         * @param wk can the white king castle kingside
         * @param wq can the white queen castle queenside
         * @param bk can the black king castle kingside
         * @param bq can the black queen castle queenside
         * @param last the last move made
         */
        public Position(Piece[][] grid, boolean wk, boolean wq, boolean bk, boolean bq, Move last) {
            this(grid);
            lastMove = last;
            if (grid[0][0] instanceof Rook && grid[0][0].getColor() == Color.BLACK && !bq) {
                movesMade[0][0] = 1;
            }
            if (grid[0][SIZE - 1] instanceof Rook && grid[0][SIZE - 1].getColor() == Color.BLACK && !bk) {
                movesMade[0][SIZE - 1] = 1;
            }
            if (grid[SIZE - 1][0] instanceof Rook && grid[SIZE - 1][0].getColor() == Color.WHITE && !wq) {
                movesMade[SIZE - 1][0] = 1;
            }
            if (grid[SIZE - 1][SIZE - 1] instanceof Rook && grid[SIZE - 1][SIZE - 1].getColor() == Color.WHITE && !wk) {
                movesMade[SIZE - 1][SIZE - 1] = 1;
            }
            // Possibly, `movesMade` does not reflect the true move count, but this constructor assumes that a lone
            // `Position` is created, with no connection to any `Board` or `Game`.
        }

        /**
         * Gets the last move made in this position.
         *
         * @return the last move made in this position
         */
        public Move getLastMove() {
            return lastMove;
        }

        // TODO: avoid looping of whole board every time (beware of ConcurrentModificationException)
        /**
         * Returns the list of pieces of a given color
         *
         * @param color a {@code Color}
         * @return the list of all pieces of color {@code color}
         */
        public HashSet<Cell> getPieceList(Color color) {
            HashSet<Cell> pieces = new HashSet<>();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    Cell cell = new Cell(i, j);
                    Piece piece = get(cell);
                    if (piece != null && piece.getColor() == color) {
                        pieces.add(cell);
                    }
                }
            }
            return pieces;
        }

        public int getPromotionRow(Color color) {
            return color == Color.WHITE ? 0 : SIZE - 1;
        }

        public int getRowBeforePromotion(Color color) {
            return color == Color.WHITE ? 1 : SIZE - 2;
        }

        /**
         * Returns the piece located at the given cell.
         *
         * @param cell a {@code Cell}
         * @return the piece located at {@code cell}, or {@code null} if there is no piece in {@code cell}
         */
        public Piece get(Cell cell) {
            return grid[cell.getRow()][cell.getCol()];
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
         * This method is private in order to enable encapsulation: it is impossible to directly alter the contents of a
         * position from outside, as {@code Position} is an inner class of {@code Board}.
         *
         * @param cell a {@code Cell}
         * @param piece a {@code Piece}
         * @param pieceMoveCount the number of moves made by {@code piece} before
         */
        private void set(Cell cell, Piece piece, int pieceMoveCount) {
            if (piece == null) {
                clear(cell);
            } else {
                int row = cell.getRow();
                int col = cell.getCol();
                grid[row][col] = piece;
                movesMade[row][col] = pieceMoveCount + 1;
                if (piece instanceof King) {
                    int index = piece.getColor() == Color.WHITE ? 0 : 1;
                    kings[index] = cell;
                }
            }
        }

        /**
         * Private method that clears the contents of a given cell.
         *
         * @param cell a {@code Cell}
         */
        private void clear(Cell cell) {
            int row = cell.getRow();
            int col = cell.getCol();
            Piece piece = grid[row][col];
            if (piece instanceof King) {
                int index = piece.getColor() == Color.WHITE ? 0 : 1;
                kings[index] = null;
            }
            grid[row][col] = null;
            movesMade[row][col] = 0;
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
         * Since move counts are affected in a non-obvious way, this method is intended to be called after a
         * corresponding call to {@link #move(Cell, Cell)}.
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
            return cell.withinBounds() && get(cell) != null;
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
         * Auxiliary method that determines whether there is a free (that is, not consisting of pieces of any color)
         * horizontal, vertical or diagonal path between two cells passed as parameters. Both endpoints are exclusive,
         * i.e. only the path strictly between the two cells is considered.
         * @param start the beginning of the path
         * @param target the end of the path
         * @param dr the row delta between two adjacent cells in the path
         * @param dc the column delta between two adjacent cells in the path
         * @return {@code true} if there is a free path between {@code first} and {@code second}, exclusive, or {@code
         * false} otherwise
         */
        private boolean isFreePathBetween(Cell start, Cell target, int dr, int dc) {
            if (start.equals(target)) {
                return true;
            }
            int startRow = start.getRow();
            int startCol = start.getCol();
            int targetRow = target.getRow();
            int targetCol = target.getCol();
            while (true) {
                startRow += dr;
                startCol += dc;
                if (startRow == targetRow && startCol == targetCol) {
                    return true;
                }
                if (isOccupied(new Cell(startRow, startCol))) {
                    return false;
                }
            }
        }

        /**
         * Determines whether there is a free (that is, not consisting of pieces of any color) horizontal, vertical or
         * diagonal path between two cells passed as parameters. Both endpoints are exclusive, i.e. only the path
         * strictly between the two cells is considered.
         *
         * @param first one endpoint
         * @param second the other endpoint
         * @return {@code true} if there is a free path between {@code first} and {@code second}, exclusive, or {@code
         * false} otherwise
         */
        public boolean isFreePathBetween(Cell first, Cell second) {
            if (first.getRow() == second.getRow()) {
                // Cells on one horizontal path
                if (first.getCol() > second.getCol()) {
                    return isFreePathBetween(second, first);
                }
                return isFreePathBetween(first, second, 0, 1);
            } else if (first.getCol() == second.getCol()) {
                // Cells on one vertical path
                if (first.getRow() > second.getRow()) {
                    return isFreePathBetween(second, first);
                }
                return isFreePathBetween(first, second, 1, 0);
            } else if (first.getCol() - first.getRow() == second.getCol() - second.getRow()) {
                // Cells on one diagonal; top-left <-> bottom-right
                if (first.getRow() > second.getRow()) {
                    return isFreePathBetween(second, first);
                }
                return isFreePathBetween(first, second, 1, 1);
            } else if (first.getCol() + first.getRow() == second.getCol() + second.getRow()) {
                // Cells on one diagonal; top-right <-> bottom-left
                if (first.getRow() > second.getRow()) {
                    return isFreePathBetween(second, first);
                }
                return isFreePathBetween(first, second, 1, -1);
            }
            throw new IllegalArgumentException("input cells " + first + " and " + second + " are not connected with" +
                    " a horizontal, vertical, or diagonal path");
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
            if (start == null) {
                return false;
            }
            Piece piece = get(start);
            if (piece == null || target == null) {
                return false;
            }
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            if (!piece.validCaptureDelta(dr, dc)) {
                return false;
            }
            if (!piece.canJump() && !(piece instanceof Pawn)) {
                return isFreePathBetween(start, target);
            }
            return true;
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
            HashSet<Cell> whitePieces = getPieceList(Color.WHITE);
            HashSet<Cell> blackPieces = getPieceList(Color.BLACK);
            for (Cell c : whitePieces) white += attacks(c, cell) ? 1 : 0;
            for (Cell c : blackPieces) black += attacks(c, cell) ? 1 : 0;
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

        public boolean isKingInCheckmate(Color color) {
            return isKingInCheck(color) && getLegalMoves(color).isEmpty();
        }

        public boolean isStalemate(Color color) {
            return !isKingInCheck(color) && getLegalMoves(color).isEmpty();
        }

        /**
         * Determines whether a move does not put one's own king in check.<br><br>
         *
         * Used to determine the entire legality of non-basic chess moves, whose initial legality is already determined
         * to be true.
         *
         * @param move a {@code Move}
         * @return {@code true} if {@code move} does not put one's own king in check, or {@code false} otherwise
         */
        public boolean isKingSafeMove(Move move) {
            Cell start = move.getStart();
            Cell target = move.getTarget();
            Piece targetPiece = get(target);
            int targetCnt = movesMade(target);
            Color color = getColor(start);
            boolean verdict = true;
            // Pretend we made the move and check whether our king becomes in check, then revert the move
            move(start, target);
            if (isKingInCheck(color)) {
                verdict = false;
            }
            unmove(start, target);
            if (targetPiece != null) {
                set(target, targetPiece, targetCnt - 1);
            }
            return verdict;
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
            if (!start.withinBounds() || !target.withinBounds()) {
                return false;
            }
            Piece piece = get(start);
            int dr = target.getRow() - start.getRow();
            int dc = target.getCol() - start.getCol();
            boolean verdict;
            if (piece == null || (!piece.canJump() && !isFreePathBetween(start, target))) {
                // Either start cell is empty or path to destination cell is blocked (and piece cannot jump)
                verdict = false;
            } else {
                HashSet<Move> additionalMoves = piece.getAdditionalLegalMoves(start, this, lastMove);
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
                return isKingSafeMove(move);
            }
            return false;
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

        public HashSet<Move> getLegalMoves(Cell cell) {
            Piece piece = get(cell);
            return piece.getLegalMoves(cell, this, lastMove);
        }

        public HashSet<Move> getLegalMoves(Color color) {
            HashSet<Cell> pieceList = getPieceList(color);
            HashSet<Move> moves = new HashSet<>();
            for (Cell cell : pieceList) {
                moves.addAll(getLegalMoves(cell));
            }
            return moves;
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

        @Override
        public Position clone() {
            Position pos = new Position(grid);
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(movesMade[i], 0, pos.movesMade[i], 0, SIZE);
            }
            // Cells are immutable, so possible to assign
            pos.kings[0] = kings[0];
            pos.kings[1] = kings[1];
            pos.lastMove = lastMove;
            return pos;
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
                Piece piece = move.getPiece();
                position.move(start, target);
                position.lastMove = move;
                int dr = target.getRow() - start.getRow();
                int dc = target.getCol() - start.getCol();
                if (piece instanceof Pawn && piece.validCaptureDelta(dr, dc) && !position.isOccupied(target)) {
                    // En passant
                    position.clear(start.shift(0, dc));
                }
            } else if (category == MoveCategory.O_O || category == MoveCategory.O_O_O) {
                Cell king = move.getStart();
                int rookColumn = (category == MoveCategory.O_O ? SIZE - 1 : 0);
                Cell rook = new Cell(king.getRow(), rookColumn);
                int multiplier = category == MoveCategory.O_O ? 1 : -1;
                position.move(rook, king.shift(0, multiplier));
            } else {
                Cell start = move.getStart();
                Cell target = move.getTarget();
                Color color = position.getColor(start);
                position.move(start, target);
                position.lastMove = move;
                if (category == MoveCategory.PROMOTE_TO_KNIGHT) {
                    position.set(target, new Knight(color), position.movesMade(target) - 1);
                } else if (category == MoveCategory.PROMOTE_TO_BISHOP) {
                    position.set(target, new Bishop(color), position.movesMade(target) - 1);
                } else if (category == MoveCategory.PROMOTE_TO_ROOK) {
                    position.set(target, new Rook(color), position.movesMade(target) - 1);
                } else {
                    position.set(target, new Queen(color), position.movesMade(target) - 1);
                }
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