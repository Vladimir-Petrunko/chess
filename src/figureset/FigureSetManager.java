package figureset;

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

    public static char getSymbol(Piece piece) {
        if (piece instanceof Pawn) {
            return figureSet.pawn(piece.getColor());
        } else if (piece instanceof Rook) {
            return figureSet.rook(piece.getColor());
        } else if (piece instanceof Knight) {
            return figureSet.knight(piece.getColor());
        } else if (piece instanceof Bishop) {
            return figureSet.bishop(piece.getColor());
        } else if (piece instanceof Queen) {
            return figureSet.queen(piece.getColor());
        } else if (piece instanceof King) {
            return figureSet.king(piece.getColor());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}