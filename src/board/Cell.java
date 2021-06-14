package board;

import utils.Pair;

public class Cell {
    private int row, col;
    public Cell(final int row, final int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public Cell shift(final int dx, final int dy) {
        return new Cell(row + dx, col + dy);
    }
    public Cell shift(final Pair pair) {
        return shift(pair.first(), pair.second());
    }
    public boolean withinBounds() {
        return 0 <= row && row < 8 && 0 <= col && col < 8;
    }
    public String coordinate() {
        return (char)(col + 'a') + "" + (8 - row);
    }
    // TODO: error handling
    public static Cell from(String coordinate) {
        int row = 8 - (coordinate.charAt(1) - '0');
        int col = coordinate.charAt(0) - 'a';
        return new Cell(row, col);
    }
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof Cell) {
            Cell cell = (Cell) other;
            return row == cell.row && col == cell.col;
        }
        return false;
    }
}