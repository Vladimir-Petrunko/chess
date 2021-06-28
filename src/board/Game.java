package board;

import player.Player;
import utils.Status;

public class Game {
    private final Board board;
    private final Player white, black;
    private Color currentTurn = Color.WHITE;

    public Game(Player white, Player black) {
        this(new Board(new Board.Position()), white, black);
    }

    public Game(Board board, Player white, Player black) {
        this.board = board;
        this.white = white;
        this.black = black;
    }

    private Status makeMove(Color turn) {
        Player current = (currentTurn == Color.WHITE ? white : black);
        Move move = current.makeMove(board.getPosition(), currentTurn);
        boolean result = board.makeMove(move);
        if (result) {
            currentTurn = Color.getOppositeColor(currentTurn);
            return Status.UNFINISHED;
        } else if (currentTurn == Color.WHITE) {
            return Status.BLACK_WON;
        } else {
            return Status.WHITE_WON;
        }
    }

    public Status nextTurn() {
        Status result = makeMove(Color.WHITE);
        if (result == Status.UNFINISHED) {
            return makeMove(Color.BLACK);
        } else {
            return result;
        }
    }

    public Status play() {
        while (true) {
            Status result = nextTurn();
            if (result != Status.UNFINISHED) {
                return result;
            }
        }
    }

}