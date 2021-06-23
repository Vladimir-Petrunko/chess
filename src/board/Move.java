package board;

import utils.Pair;

public class Move {
    private final Cell start, target;
    private final MoveCategory category;

    public Move(final Cell start, final Cell target) {
        this(start, target, MoveCategory.ORDINARY);
    }

    public Move(final Cell start, final Cell target, final MoveCategory category) {
        this.start = start;
        this.target = target;
        this.category = category;
    }

    public Cell getStart() {
        return start;
    }

    public Cell getTarget() {
        return target;
    }

    public MoveCategory getCategory() {
        return category;
    }

    public Pair getDelta() {
        return new Pair(target.getRow() - start.getRow(), target.getCol() - start.getCol());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Move) {
            Move move = (Move) other;
            return move.start.equals(start) && move.target.equals(target) && move.category == category;
        }
        return false;
    }
}