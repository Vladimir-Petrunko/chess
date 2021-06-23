package pieces;

import java.util.List;
import java.util.ArrayList;
import board.Color;
import utils.Pair;

public class Bishop extends Piece {
    private static final List<Pair> deltas = new ArrayList<>();

    static {
        for (int delta = -7; delta <= 7; delta++) {
            deltas.add(new Pair(delta, delta));
            deltas.add(new Pair(delta, -delta));
        }
    }

    public Bishop(final Color color) {
        super(color);
    }

    @Override
    public boolean validMoveDelta(final int dr, final int dc) {
        return Math.abs(dr) == Math.abs(dc);
    }

    @Override
    public List<Pair> getBasicDeltas() {
        return deltas;
    }
}
