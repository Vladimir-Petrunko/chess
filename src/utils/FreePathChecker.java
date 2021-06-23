package utils;

import board.Cell;

public class FreePathChecker {
    private final int n;
    private final boolean[][] grid;
    private final int[][][] steps;
    private static final int RIGHT = 0;
    private static final int DOWN = 1;
    private static final int RIGHT_DOWN = 2;
    private static final int LEFT_DOWN = 3;

    public FreePathChecker(int n) {
        this.n = n;
        steps = new int[4][n][n];
        grid = new boolean[n][n];
        update();
    }

    // TODO: optimize to O(n), currently O(n^3) brute force
    private void update() {
        for (int id = 0; id < 4; id++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    steps[id][i][j] = 0;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int delta = 1; delta <= n; delta++) {
                    if (i + delta >= n || grid[i + delta][j]) {
                        steps[DOWN][i][j] = delta - 1;
                        break;
                    }
                }
                for (int delta = 1; delta <= n; delta++) {
                    if (j + delta >= n || grid[i][j + delta]) {
                        steps[RIGHT][i][j] = delta - 1;
                        break;
                    }
                }
                for (int delta = 1; delta <= n; delta++) {
                    if (i + delta >= n || j + delta >= n || grid[i + delta][j + delta]) {
                        steps[RIGHT_DOWN][i][j] = delta - 1;
                        break;
                    }
                }
                for (int delta = 1; delta <= n; delta++) {
                    if (i + delta >= n || j - delta < 0 || grid[i + delta][j - delta]) {
                        steps[LEFT_DOWN][i][j] = delta - 1;
                        break;
                    }
                }
            }
        }
    }

    public void set(final int i, final int j) {
        grid[i][j] = true;
        update();
    }

    public void remove(final int i, final int j) {
        grid[i][j] = false;
        update();
    }

    public boolean isFreePathBetween(final Cell start, final Cell target) {
        if (start.getRow() == target.getRow()) {
            // Same row
            Cell init = start.getCol() < target.getCol() ? start : target;
            int count = steps[RIGHT][init.getRow()][init.getCol()];
            return count >= Math.abs(start.getRow() - target.getRow()) - 1;
        } else if (start.getCol() == target.getCol()) {
            // Same column
            Cell init = start.getRow() < target.getRow() ? start : target;
            int count = steps[DOWN][init.getRow()][init.getCol()];
            return count >= Math.abs(start.getCol() - target.getCol()) - 1;
        } else if (start.getRow() + start.getCol() == target.getRow() + target.getCol()) {
            // Parallel to main diagonal
            Cell init = start.getRow() < target.getRow() ? start : target;
            int count = steps[RIGHT_DOWN][init.getRow()][init.getCol()];
            return count >= Math.abs(start.getRow() - target.getRow()) - 1;
        } else if (start.getRow() - start.getCol() == target.getRow() + target.getCol()) {
            // Parallel to side diagonal
            Cell init = start.getRow() < target.getRow() ? start : target;
            int count = steps[LEFT_DOWN][init.getRow()][init.getCol()];
            return count >= Math.abs(start.getRow() - target.getRow()) - 1;
        } else {
            return true;
        }
    }
}