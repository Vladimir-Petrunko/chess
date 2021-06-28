package pieces;

import java.util.List;
import java.util.ArrayList;

import board.Board.Position;
import board.Cell;
import board.Color;
import board.Move;
import utils.Pair;
import figureset.FigureSetManager;

public abstract class Piece {
    protected final Color color;

    /**
     * Initializes an object of type {@link Piece} with the given color. Note that the color is a necessary part of
     * a piece's description, so there is no default (no-argument) constructor.
     *
     * @param color a {@code Color}
     */
    public Piece(Color color) {
        this.color = color;
    }

    /**
     * @return the color of this piece
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return {@code true} if this is an instance of {@link Knight} (and thus is able to jump over other pieces during
     * a move), and {@code false} otherwise
     */
    public boolean canJump() {
        return false;
    }

    /**
     * Returns the symbol of this piece. A piece can have several possible symbolic representations. The representation
     * in use currently is determined by the active {@code FigureSet} (by default {@code FigurineFigureSet} is used).
     *
     * @return the symbol of this piece in accordance with the current {@code FigureSet}
     *
     * @see figureset.FigureSet FigureSet
     * @see figureset.FigureSetManager FigureSetManager
     */
    public char getSymbol() {
        return FigureSetManager.getSymbol(this);
    }

    /**
     * @param dr the row displacement
     * @param dc the column displacement
     *
     * @return {@code true} if {@code (dr, dc)} is a valid shift of this piece during an ordinary, non-capture move
     */
    public abstract boolean validMoveDelta(int dr, int dc);

    /**
     * This method has the same logic as {@code validMoveDelta} for every piece except {@code Pawn}, so default logic
     * is provided.
     *
     * @param dr the row displacement
     * @param dc the column displacement
     *
     * @return {@code true} if {@code (dr, dc)} is a valid shift of this piece during an ordinary, capture move
     */
    public boolean validCaptureDelta(int dr, int dc) {
        return validMoveDelta(dr, dc);
    }

    /**
     * Returns a list of pairs representing all possible shifts of a chess piece of this type on a board with dimensions
     * {@code SIZE * SIZE}, where {@code SIZE} is global across the program. All types of moves are considered, except
     * castling and pawn captures.<br><br>
     *
     * It is not guaranteed whatsoever that all of these shifts are valid in a given position, but
     * the inverse is true: if in a given position an ordinary move is being made, the shift from the initial cell to
     * the destination cell is included in the list.<br><br>
     *
     * Basically, this method describes the motion rules of a piece of this type.
     *
     * @return a list of valid shifts of a piece of this type.
     */
    public abstract List<Pair> getBasicDeltas();

    /**
     * Returns a list of ordinary legal moves of this chess piece on a given {@code Position}. Specifically, the
     * following types of moves <b>are not considered</b>:
     * <ul>
     *     <li>"En passant" pawn captures,</li>
     *     <li>Pawn promotions,</li>
     *     <li>Castling</li>
     * </ul>
     * A separate method deals with these types of moves.
     *
     * @param initial the initial cell of this piece
     * @param position a {@code Position}
     *
     * @return a list of ordinary legal moves of this piece in {@code position}
     *
     * @see #getAdditionalLegalMoves(Cell, Position, Move) getAdditionalLegalMoves(Cell, Position, Move)
     */
    public List<Move> getBasicLegalMoves(Cell initial, Position position) {
        List<Move> moves = new ArrayList<>();
        List<Pair> deltas = getBasicDeltas();
        for (Pair delta : deltas) {
            // For each shift, check whether it is legal in the current position
            Cell shifted = initial.shift(delta.first(), delta.second());
            Move move = new Move(initial, shifted, this);
            if (position.isLegalMove(move)) {
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Returns a list of additional legal moves of this chess piece on a given {@code Position}. Specifically, the
     * following types of moves <b>are considered</b>:
     * <ul>
     *      <li>En passant pawn captures,</li>
     *      <li>Pawn promotions,</li>
     *      <li>Castling</li>
     * </ul>
     * A separate method deals with the other types of moves.<br><br>
     *
     * The parameter {@code lastMove} is used for checking the legality of en passant captures: by the rules of chess,
     * such a capture can only be made in a one-move window.<br><br>
     *
     * Most pieces do not have additional moves, so by default this method returns an empty list.
     *
     * @param initial the initial cell of this piece
     * @param position a {@code Position}
     * @param lastMove the last move made in the position
     *
     * @return a list of additional legal moves of this piece in {@code position}
     *
     * @see #getBasicLegalMoves(Cell, Position) getBasicLegalMoves(Cell, Position)
     */
    public List<Move> getAdditionalLegalMoves(Cell initial, Position position, Move lastMove) {
        return new ArrayList<>();
    }

    /**
     * Returns a list of <b>all</b> legal moves of this piece on a given {@code Position}.<br><br>
     *
     * For details, see the previous two methods. This method just combines their output.
     *
     * @param initial the initial cell of this piece
     * @param position a {@code Position}
     * @param lastMove the last move made in this position
     *
     * @return a list of all legal moves of this piece in {@code position}
     *
     * @see #getAdditionalLegalMoves(Cell, Position, Move) getAdditionalLegalMoves(Cell, Position, Move)
     * @see #getBasicLegalMoves(Cell, Position) <br>getBasicLegalMoves(Cell, Position)
     */
    public List<Move> getLegalMoves(Cell initial, Position position, Move lastMove) {
        List<Move> basic = getBasicLegalMoves(initial, position);
        List<Move> moves = new ArrayList<>(basic);
        List<Move> additional = getAdditionalLegalMoves(initial, position, lastMove);
        moves.addAll(additional);
        return moves;
    }

    /**
     * Computes the hash code of this piece in such a way as to return distinct hash codes for every piece type and
     * color combination).
     */
    @Override
    public int hashCode() {
        int value;
        switch(getSymbol()) {
            case 'P' -> value = 0;
            case 'R' -> value = 2;
            case 'N' -> value = 4;
            case 'B' -> value = 6;
            case 'Q' -> value = 8;
            default -> value = 10; // case 'K'
        }
        return color == Color.WHITE ? value : value + 1;
    }
}