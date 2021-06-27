package board;

import pieces.Piece;
import pieces.King;
import pieces.Pawn;

import static utils.Global.SIZE;

public class Move {
    // To avoid unnecessary recalculations in .hashCode()
    private static final int BOARD_SIZE = SIZE * SIZE;
    private static final int BOARD_SIZE_SQ = BOARD_SIZE * BOARD_SIZE;

    // Immutable fields representing the structure of the move
    private final Cell start, target;
    private final Piece piece;
    private final String promoted;

    private MoveCategory category;

    /**
     * Constructor of {@code Move}. <br><br>
     *
     * This constructor should be used for moves (except pawn promotions), whose category is not yet known (for example,
     * moves read from the input stream). <br><br>
     *
     * By default {@code promoted} is set to {@code null}, which means that the current move is not a pawn promotion.
     * Moves that are pawn promotions should not use this constructor. <br><br>
     *
     * The move's category is set automatically (see {@link #categorize()}). It is the caller's responsibility to ensure
     * that the parameters are set correctly, otherwise, the move can be mistakenly set the wrong category. <br><br>
     *
     * If this is a castling move, then {@code target} should be the cell of the corresponding rook. Otherwise, {@code
     * target} should be the destination cell of the piece making the move.
     *
     * @param start the source cell (i.e. where the piece moved <i>from</i>)
     * @param target the target cell (i.e. where the piece moved <i>to</i>, except for castling)
     * @param piece the piece that made the move
     *
     * @see #Move(Cell, Cell, Piece, String) Move(Cell, Cell, Piece, String)
     * @see #Move(Cell, Cell, Piece, MoveCategory) <br>Move(Cell, Cell, Piece, MoveCategory)
     */
    public Move(Cell start, Cell target, Piece piece) {
        this(start, target, piece, MoveCategory.UNCATEGORIZED, null);
    }

    /**
     * Constructor of {@code Move}.
     * <br><br>
     *
     * This constructor should only be used for pawn promotions. {@code promoted} is one of {@code {"Q", "R", "B", "N"}}
     * denoting the piece that the pawn promoted to. It is the caller's responsibility to ensure that all the parameters
     * are correct, otherwise the move can be mistakenly set the wrong category. <br><br>
     *
     * As in the other constructors, the move's category is set automatically (see {@link #categorize()}).
     *
     * @param start the source cell (i.e. where the piece moved <i>from</i>)
     * @param target the target cell (i.e. where the piece moved <i>to</i>)
     * @param piece the piece that made the move (should have type {@code Pawn})
     * @param promoted the symbol of the promoted piece
     *
     * @see #Move(Cell, Cell, Piece) Move(Cell, Cell, Piece)
     * @see #Move(Cell, Cell, Piece, MoveCategory) <br>Move(Cell, Cell, Piece, MoveCategory)
     */
    public Move(Cell start, Cell target, Piece piece, String promoted) {
        this(start, target, piece, MoveCategory.UNCATEGORIZED, promoted);
    }

    /**
     * Constructor of {@code Move}.
     * <br><br>
     *
     * This constructor should be used for moves (except pawn promotions), whose category is already known (for example,
     * algorithm-generated moves). <br><br>
     *
     * By default {@code promoted} is set to {@code null}, which means that the current move is not a pawn promotion.
     * Moves that are pawn promotions should not use this constructor. <br><br>
     *
     * It is the caller's responsibility to ensure that the parameters are consistent and denote a valid move.
     *
     * @param start the source cell (i.e. where the piece moved <i>from</i>)
     * @param target the target cell (i.e. where the piece moved <i>to</i>) or cell of corresponding rook if castling
     * @param piece the piece that made the move
     * @param category a {@code MoveCategory}
     *
     * @see #Move(Cell, Cell, Piece) Move(Cell, Cell, Piece)
     * @see #Move(Cell, Cell, Piece, String) <br>Move(Cell, Cell, Piece, String)
     */
    public Move(Cell start, Cell target, Piece piece, MoveCategory category) {
        this(start, target, piece, category, null);
    }

    /**
     * Private constructor setting all private fields of this move from parameters.
     *
     * @param start the source cell (i.e. where the piece moved <i>from</i>)
     * @param target the target cell (i.e. where the piece moved <i>to</i>) or cell of corresponding rook if castling
     * @param piece the piece that made the move
     * @param category a {@code MoveCategory}
     * @param promoted the symbol of the promoted cell, or {@code null} if not a promotion
     */
    private Move(Cell start, Cell target, Piece piece, MoveCategory category, String promoted) {
        this.start = start;
        this.target = target;
        this.piece = piece;
        this.category = category;
        this.promoted = promoted;
        categorize();
    }

    /**
     * @return the source cell (i.e. where the piece moved <i>from</i>)
     */
    public Cell getStart() {
        return start;
    }

    /**
     * @return the destination cell (i.e. where the piece moved <i>to</i>) or cell of corresponding rook if castling
     */
    public Cell getTarget() {
        return target;
    }

    /**
     * @return the piece that made this move
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * @return the category of this move
     */
    public MoveCategory getCategory() {
        return category;
    }

    /**
     * Private method that assigns a category to this move, if not already. <br><br>
     *
     * Based on the structure of the current move, determines whether it is a special move (i.e. castling, pawn
     * promotion or en passant capture) or not, and updates {@code category} accordingly. <br><br>
     *
     * Assumes that the move is valid. If parameters are incorrect, the move can be categorized incorrectly.
     */
    private void categorize() {
        if (category == MoveCategory.UNCATEGORIZED) {
            // Ordinary move until proved otherwise
            category = MoveCategory.ORDINARY;
            if (piece instanceof King) {
                // Possible castling
                if (target.getCol() - start.getCol() > 1) {
                    // Big king move to the right => kingside castling
                    category = MoveCategory.O_O;
                } else if (start.getCol() - target.getCol() > 1) {
                    // Big king move to the left => queenside castling
                    category = MoveCategory.O_O_O;
                }
            } else if (piece instanceof Pawn) {
                int promotionRow = (piece.getColor() == Color.WHITE ? 0 : SIZE - 1);
                if (target.getRow() == promotionRow) {
                    // Pawn promotion
                    switch (promoted) {
                        case "Q" -> category = MoveCategory.PROMOTE_TO_QUEEN;
                        case "R" -> category = MoveCategory.PROMOTE_TO_ROOK;
                        case "B" -> category = MoveCategory.PROMOTE_TO_BISHOP;
                        default -> category = MoveCategory.PROMOTE_TO_KNIGHT; // case "N"
                    }
                } else if (promoted == null && start.getCol() != target.getCol()) {
                    // Capture was made to an empty cell => en passant
                    category = MoveCategory.EN_PASSANT;
                }
            }
        }
    }

    /**
     * This method allows to store objects of type {@code Move} correctly in collections, since not all fields
     * are used to determine whether two moves are the same.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Move) {
            Move move = (Move) other;
            return start.equals(move.start) && target.equals(move.target) && piece.getSymbol() == move.piece.getSymbol();
        }
        return false;
    }

    /**
     * Represents a move in full chess notation, {@code Ax0-y0}, where:
     * <ul>
     *     <li>{@code A} is the symbol of the piece making the move,</li>
     *     <li>{@code x0} is the coordinate of the start cell,</li>
     *     <li>{@code x1} is the coordinate of the target cell</li>
     * </ul>
     *
     * @return the move representation in full chess notation.
     */
    @Override
    public String toString() {
        return piece.getSymbol() + "" + start + "-" + target;
    }

    /**
     * Computes the hash code of this move. It is guaranteed that, independent of the size of the board, there is a
     * one-to-one mapping between hash codes of moves and non-negative integers in the interval {@code [0, N]} for
     * some {@code N}. If the size is small enough (but it can still be significantly larger than the default value
     * of 8), these hash codes fit into {@code int} and thus are distinct.
     * <br><br>
     *
     * Allows for more efficient storage of objects of type {@code Move} in hash tables.
     *
     * @return the computed hash code
     */
    @Override
    public int hashCode() {
        return piece.hashCode() * BOARD_SIZE_SQ + start.hashCode() * BOARD_SIZE + target.hashCode();
    }
}