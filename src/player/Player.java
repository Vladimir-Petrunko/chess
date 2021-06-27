package player;

import board.Move;
import board.Color;
import board.Board.Position;

public interface Player {
    /**
     * Returns a legal move to be made in a given position with pieces of a given color. Different classes may provide
     * their own logic behind choosing a candidate move.
     *
     * @param position the current game position
     * @param color the color of the pieces whose move it is now
     *
     * @return a legal move in the current position
     */
    Move makeMove(final Position position, final Color color);
    String getNickname();
}
