package figureset;

import pieces.*;

public class FigureSetManager {
    private static FigureSet figureSet = new FigurineFigureSet();

    public static void setFigureSet(final FigureSet set) {
        figureSet = set;
    }

    public static char getSymbol(final Piece piece) {
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