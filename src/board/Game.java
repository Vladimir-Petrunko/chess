package board;

import player.Player;
import utils.Status;

public class Game {
    private final Board board;
    private final Player white, black;
    private Color currentTurn = Color.WHITE;

    /**
     * Default constructor of {@code Game} from two players and a default chessboard.
     *
     * @param white the white {@code Player}
     * @param black the black {@code Player}
     */
    public Game(Player white, Player black) {
        this(new Board(new Board.Position()), white, black);
    }

    /**
     * Initializes a new {@code Game} from two players and a given board.
     *
     * @param board a {@code Board}
     * @param white the white {@code Player}
     * @param black the white {@code Player}
     */
    public Game(Board board, Player white, Player black) {
        this.board = board;
        this.white = white;
        this.black = black;
    }

    /**
     * Makes a half-move in the game on the behalf of the player whose turn it is to move.
     *
     * @return the status of the game after the half-move
     */
    private Status makeMove() {
        Player current = (currentTurn == Color.WHITE ? white : black);
        Move move = current.makeMove(board.getPosition(), currentTurn);
        boolean result = board.makeMove(move);
        // If move is legal, continue the game. Otherwise - automatic forfeit (since it is the Player's responsibility
        // to provide legal moves; he has all the necessary information
        if (result) {
            currentTurn = Color.getOppositeColor(currentTurn);
            return Status.UNFINISHED;
        } else if (currentTurn == Color.WHITE) {
            return Status.BLACK_WON;
        } else {
            return Status.WHITE_WON;
        }
    }

    /**
     * Makes a full move in the game (first white's turn, then black's).
     *
     * @return the status of the game after the full move
     */
    public Status nextTurn() {
        Status result = makeMove();
        if (result == Status.UNFINISHED) {
            // Game not decided, we can make the next half-move
            return makeMove();
        } else {
            return result;
        }
    }

    /**
     * Plays the game from the beginning up to the end and returns the result.<br><br>
     *
     * This method is supposed to be called only once for a single instance of {@code Board}. It starts the game and
     * loops repeatedly until one of the players wins or a draw is reached.
     *
     * @return the status of the game after the end
     */
    public Status play() {
        while (true) {
            Status result = nextTurn();
            if (result != Status.UNFINISHED) {
                return result;
            }
        }
    }

}