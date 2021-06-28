package pieces;

import java.util.List;
import java.util.ArrayList;
import board.Color;
import utils.Pair;
import static utils.Global.SIZE;

public class Bishop extends Piece {
    private static final List<Pair> deltas = new ArrayList<>();

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
    public List<Pair> getBasicDeltas() {
        return deltas;
    }
}
