package pieces;

import java.util.HashSet;
import board.Color;
import utils.Pair;

import static utils.Global.SIZE;

public class Rook extends Piece {
    private static final HashSet<Pair> deltas = new HashSet<Pair>();

    static {
        for (int delta = -SIZE + 1; delta <= SIZE - 1; delta++) {
            deltas.add(new Pair(0, delta));
            deltas.add(new Pair(delta, 0));
        }
    }

    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean validMoveDelta(int dr, int dc) {
        return dr == 0 || dc == 0;
    }

    @Override
    public HashSet<Pair> getBasicDeltas() {
        return deltas;
    }
}
