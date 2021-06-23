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
    public boolean validMoveDelta(final int dx, final int dy) {
        return (Math.abs(dx) == 1 && Math.abs(dy) == 2) ||
               (Math.abs(dx) == 2 && Math.abs(dy) == 1);
    }

    @Override
    public List<Pair> getBasicDeltas() {
        return deltas;
    }
}
