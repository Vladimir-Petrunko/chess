package pieces;

import java.util.List;
import java.util.ArrayList;
import board.Color;
import utils.Pair;

import static utils.Global.SIZE;

public class Queen extends Piece {
    private static final List<Pair> deltas = new ArrayList<Pair>();

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
    public List<Pair> getBasicDeltas() {
        return deltas;
    }
}
