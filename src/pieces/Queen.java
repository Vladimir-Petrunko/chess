package pieces;

import java.util.HashSet;
import board.Color;
import utils.Pair;

import static utils.Global.SIZE;

public class Queen extends Piece {
    private static final HashSet<Pair> deltas = new HashSet<Pair>();

    static {
        for (int delta = -SIZE + 1; delta <= SIZE - 1; delta++) {
            deltas.add(new Pair(delta, delta));
            deltas.add(new Pair(delta, -delta));
            deltas.add(new Pair(0, delta));
            deltas.add(new Pair(delta, 0));
        }
    }

    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return Math.abs(dr) == Math.abs(dc) || dc == 0 || dr == 0;
    }

    @Override
    public HashSet<Pair> getBasicDeltas() {
        return deltas;
    }
}
