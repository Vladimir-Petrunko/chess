package figureset;

import board.Color;

public interface FigureSet {
    char pawn(final Color color);
    char rook(final Color color);
    char knight(final Color color);
    char bishop(final Color color);
    char queen(final Color color);
    char king(final Color color);
}
