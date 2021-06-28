package figureset;

import board.Color;

public interface FigureSet {
    /**
     * @param color a {@code Color}
     * @return the character for a pawn of color {@code color}
     */
    char pawn(Color color);

    /**
     * @param color a {@code Color}
     * @return the character for a rook of color {@code color}
     */
    char rook(Color color);

    /**
     * @param color a {@code Color}
     * @return the character for a knight of color {@code color}
     */
    char knight(Color color);

    /**
     * @param color a {@code Color}
     * @return the character for a bishop of color {@code color}
     */
    char bishop(Color color);

    /**
     * @param color a {@code Color}
     * @return the character for a queen of color {@code color}
     */
    char queen(Color color);

    /**
     * @param color a {@code Color}
     * @return the character for a king of color {@code color}
     */
    char king(Color color);
}
