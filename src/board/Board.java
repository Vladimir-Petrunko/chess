package board;

import pieces.*;

import java.util.*;

public class Board {
    public static final int SIZE = 8;
    Piece[][] board = new Piece[SIZE][SIZE];
    public Board() {
        // TODO: remove copy paste
        // Pawns
        for (int i = 0; i < SIZE; i++) {
            board[1][i] = new Pawn(Color.BLACK);
            board[6][i] = new Pawn(Color.WHITE);
        }
        // Black pieces
        board[0][0] = new Rook(Color.BLACK);
        board[0][1] = new Knight(Color.BLACK);
        board[0][2] = new Bishop(Color.BLACK);
        board[0][3] = new Queen(Color.BLACK);
        board[0][4] = new King(Color.BLACK);
        board[0][5] = new Bishop(Color.BLACK);
        board[0][6] = new Knight(Color.BLACK);
        board[0][7] = new Rook(Color.BLACK);
        // White pieces
        board[7][0] = new Rook(Color.WHITE);
        board[7][1] = new Knight(Color.WHITE);
        board[7][2] = new Bishop(Color.WHITE);
        board[7][3] = new Queen(Color.WHITE);
        board[7][4] = new King(Color.WHITE);
        board[7][5] = new Bishop(Color.WHITE);
        board[7][6] = new Knight(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);
    }

    public Piece get(Cell cell) {
        return board[cell.getRow()][cell.getCol()];
    }

    public boolean canOccupy(Cell cell, Piece piece) {
        Piece p = get(cell);
        return p == null || p.getColor() != piece.getColor();
    }

    public boolean isFreePathBetween(Cell start, Cell finish) {
        int dx = finish.getRow() - start.getRow();
        int dy = finish.getCol() - start.getCol();
        int div = Math.max(Math.abs(dx), Math.abs(dy));
        dx /= div;
        dy /= div;
        for (Cell cell = start.shift(dx, dy); !cell.equals(finish); cell = cell.shift(dx, dy)) {
            if (get(cell) != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isLegalMove(Cell initial, Cell target, Cell lastMove) {
        Piece piece = get(initial);
        if (piece == null) {
            return false;
        }
        ArrayList<Cell> legalMoves = piece.getLegalMoves(initial, this, lastMove);
        return legalMoves.contains(target);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j] == null ? "." : board[i][j].getSymbol()).append('\t');
            }
            sb.append((8 - i));
            sb.append('\n');
        }
        for (int i = 0; i < SIZE; i++) {
            sb.append((char)('a' + i)).append('\t');
        }
        return sb.toString();
    }
}