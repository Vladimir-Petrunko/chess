package board;

import static utils.Global.SIZE;

public class Cell {
    // Coordinates of cell (0-indexed)
    private final int row, col;

    /**
     * Constructor of {@code Cell}. Just sets private fields.<br><br>
     *
     * Note that both the row and column are numbered from 0.
     *
     * @param row the cell's row index
     * @param col the cell's column index
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructor of {@code Cell} from a coordinate in chess notation (an English letter denoting the rank (i.e. row),
     * followed by a digit denoting the file (i.e. column)). Ranges may differ within reasonable limits depending on
     * the board size.
     *
     * @param coordinate the coordinate of the cell in chess notation, as described above
     */
    public Cell(String coordinate) {
        if (coordinate.length() != 2) {
            throw new IllegalArgumentException(coordinate + " is not a valid cell on a " + SIZE + "*" + SIZE + " chessboard.");
        }
        this.row = SIZE - (Character.toLowerCase(coordinate.charAt(1)) - '0');
        this.col = coordinate.charAt(0) - 'a';
        if (!withinBounds()) {
            throw new IllegalArgumentException(coordinate + " is not a valid cell on a " + SIZE + "*" + SIZE + " chessboard.");
        }
    }

    /**
     * @return the row index of this cell
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index of this cell
     */
    public int getCol() {
        return col;
    }

    /**
     * @param dr the row displacement
     * @param dc the column displacement
     * @return a copy of this cell, obtained by shifting this cell by {@code (dr, dc)}
     */
    public Cell shift(int dr, int dc) {
        return new Cell(row + dr, col + dc);
    }

    /**
     * @return {@code true} if this cell is within bounds of a board of the current size, {@code false} otherwise
     */
    public boolean withinBounds() {
        return 0 <= row && row < SIZE && 0 <= col && col < SIZE;
    }

    /**
     * Determines whether this cell is beside (i.e. to the left or right) another cell passed as a parameter.
     *
     * @param other a {@code Cell}
     * @return {@code true} if this cell is beside {@code other}, {@code false} otherwise
     */
    public boolean isBeside(Cell other) {
        int dr = other.getRow() - getRow();
        int dc = other.getCol() - getCol();
        return dr == 0 && Math.abs(dc) == 1;
    }

    /**
     * @return the string representation of this cell in chess notation
     *
     * @see #Cell(String) Cell(String)
     */
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

    /**
     * Computes the hash code of this cell in such a way as to return distinct hash codes for distinct cells (as long
     * as the result fits in Java's {@code int}.
     */
    @Override
    public int hashCode() {
        return row * SIZE + col;
    }
}