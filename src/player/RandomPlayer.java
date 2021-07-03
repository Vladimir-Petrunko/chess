package player;

import board.Color;
import board.Move;
import board.Board.Position;

import java.util.HashSet;

public class RandomPlayer implements Player {
    private final String nickname;

    public RandomPlayer(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Move makeMove(Position position, Color color) {
        HashSet<Move> moves = position.getLegalMoves(color);
        Move[] arr = new Move[moves.size()];
        moves.toArray(arr);
        int rand = (int)(Math.random() * arr.length);
        return arr[rand];
    }
}