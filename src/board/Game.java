package board;

import pieces.Pawn;
import player.Player;
import board.Board.Position;
import utils.Status;

public class Game {
    private final Board board;
    private final Player white, black;
    private Color currentTurn = Color.WHITE;
    private Status gameResult = Status.UNFINISHED;
    private StringBuilder notation = new StringBuilder();
    private int moveNumber = 1;
    private int halfMovesUntilDraw = 100;

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
        if (move.isCapture(board.getPosition()) || move.getPiece() instanceof Pawn) {
            halfMovesUntilDraw = 100;
        } else {
            halfMovesUntilDraw--;
        }
        StringBuilder str = new StringBuilder(move.toString());
        while (str.length() < 10) {
            str.append(' ');
        }
        if (currentTurn == Color.WHITE) {
            if (moveNumber != 0) notation.append('\n');
            notation.append(moveNumber).append('.');
            if (moveNumber < 10) {
                notation.append("     ");
            } else if (moveNumber < 100) {
                notation.append("    ");
            } else if (moveNumber < 1000) {
                notation.append("   ");
            } else {
                notation.append("  ");
            }
            notation.append(str);
        } else {
            notation.append("\t\t").append(str);
        }
        boolean result = board.makeMove(move);
        // If move is legal, continue the game. Otherwise - automatic forfeit (since it is the Player's responsibility
        // to provide legal moves; he has all the necessary information
        if (result) {
            currentTurn = Color.getOppositeColor(currentTurn);
            Position position = board.getPosition();
            if (position.isKingInCheckmate(currentTurn)) {
                return currentTurn == Color.WHITE ? Status.BLACK_WON : Status.WHITE_WON;
            } else if (position.isStalemate(currentTurn) || halfMovesUntilDraw <= 0) {
                return Status.DRAW;
            } else {
                return Status.UNFINISHED;
            }
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
            result = makeMove();
            moveNumber++;
        }
        return result;
    }

    /**
     * Plays the game from the beginning up to the end and returns the result.<br><br>
     *
     * This method is supposed to be called only once for a single instance of {@code Board}. It starts the game and
     * loops repeatedly until one of the players wins or a draw is reached. If this method is called more than once,
     * all subsequent calls return the result of the first call.
     *
     * @return the status of the game after the end
     */
    public Status play() {
        if (gameResult != Status.UNFINISHED) {
            return gameResult;
        }
        while (true) {
            Status result = nextTurn();
            if (result != Status.UNFINISHED) {
                gameResult = result;
                if (gameResult == Status.BLACK_WON) {
                    notation.append('\n').append("0-1");
                } else if (gameResult == Status.WHITE_WON) {
                    notation.append('\n').append("1-0");
                } else {
                    notation.append('\n').append("½-½");
                }
                return gameResult;
            }
        }
    }

    public String getGameNotation() {
        return notation.toString();
    }

    public Position getFinalPosition() {
        return board.getPosition();
    }

}