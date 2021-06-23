package board;

import static utils.Global.SIZE;

public class Cell {
    private final int row, col;

    public Cell(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    public Cell(final String coordinate) {
        this.row = SIZE - (coordinate.charAt(1) - '0');
        this.col = coordinate.charAt(0) - 'a';
        if (!withinBounds()) {
            throw new IllegalArgumentException(coordinate + " is not a valid cell on a " + SIZE + "*" + SIZE + " chessboard.");
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Cell shift(final int dr, final int dc) {
        return new Cell(row + dr, col + dc);
    }

    public boolean withinBounds() {
        return 0 <= row && row < SIZE && 0 <= col && col < SIZE;
    }

    @Override
    public String toString() {
        return (char)(col + 'a') + "" + (SIZE - row);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Cell) {
            Cell cell = (Cell) other;
            return row == cell.row && col == cell.col;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return row * SIZE + col;
    }
}