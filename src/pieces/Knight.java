package pieces;

import java.util.HashSet;
import board.Color;
import utils.Pair;

public class Knight extends Piece {
    private static final HashSet<Pair> deltas = new HashSet<>();

    static {
        deltas.add(new Pair(1, 2));
        deltas.add(new Pair(1, -2));
        deltas.add(new Pair(-1, 2));
        deltas.add(new Pair(-1, -2));
        deltas.add(new Pair(2, 1));
        deltas.add(new Pair(2, -1));
        deltas.add(new Pair(-2, 1));
        deltas.add(new Pair(-2, -1));
    }

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
    public HashSet<Pair> getBasicDeltas() {
        return deltas;
    }
}
