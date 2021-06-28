package pieces;

import java.util.List;
import board.Color;
import utils.Pair;

public class Knight extends Piece {
    private static final List<Pair> deltas = List.of(
        new Pair(1, 2),
        new Pair(1, -2),
        new Pair(-1, 2),
        new Pair(-1, -2),
        new Pair(2, 1),
        new Pair(2, -1),
        new Pair(-2, 1),
        new Pair(-2, -1)
    );

    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return (Math.abs(dr) == 1 && Math.abs(dc) == 2) ||
               (Math.abs(dr) == 2 && Math.abs(dc) == 1);
    }

    @Override
    public List<Pair> getBasicDeltas() {
        return deltas;
    }
}
