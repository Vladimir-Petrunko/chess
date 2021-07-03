package pieces;

import java.util.HashSet;
import board.Color;
import utils.Pair;
import static utils.Global.SIZE;

public class Bishop extends Piece {
    private static final HashSet<Pair> deltas = new HashSet<>();

    static {
        for (int delta = -SIZE + 1; delta <= SIZE - 1; delta++) {
            deltas.add(new Pair(delta, delta));
            deltas.add(new Pair(delta, -delta));
        }
    }

    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return Math.abs(dr) == Math.abs(dc);
    }

    @Override
    public HashSet<Pair> getBasicDeltas() {
        return deltas;
    }
}
