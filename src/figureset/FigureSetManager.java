package figureset;

import board.Color;
import pieces.*;

public class FigureSetManager {
    private static FigureSet figureSet = new FigurineFigureSet();

    /**
     * Sets the active {@code FigureSet}.<br><br>
     *
     * A {@code FigureSet} is a group of rules mapping each piece to a character representation. Different figure sets
     * denote different mappings. By default the {@code FigurineFigureSet} is used.
     *
     * @param set the {@code FigureSet} to be used
     */
    public static void setFigureSet(FigureSet set) {
        figureSet = set;
    }

    private static char getSymbol(Piece piece, Color color) {
        if (piece instanceof Pawn) {
            return figureSet.pawn(color);
        } else if (piece instanceof Rook) {
            return figureSet.rook(color);
        } else if (piece instanceof Knight) {
            return figureSet.knight(color);
        } else if (piece instanceof Bishop) {
            return figureSet.bishop(color);
        } else if (piece instanceof Queen) {
            return figureSet.queen(color);
        } else if (piece instanceof King) {
            return figureSet.king(color);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static char getSymbol(Piece piece) {
        return getSymbol(piece, piece.getColor());
    }

    public static char getNotationSymbol(Piece piece) {
        return getSymbol(piece, Color.WHITE);
    }
}