import board.*;
import player.*;
import utils.*;
import pieces.*;

public class Main {

    public static void main(String[] args) {
        Piece[][] grid = new Piece[][]{
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, new King(Color.BLACK), null, null, null, null},
                {null, null, null, new Pawn(Color.BLACK), null, null, null, null},
                {null, null, null, new Pawn(Color.WHITE), null, null, null, null},
                {null, null, null, new King(Color.WHITE), null, null, null, null},
                {null, null, new Queen(Color.BLACK), null, null, null, null, null},
                {null, null, null, null, new Knight(Color.BLACK), null, null, null}
        };
        Board.Position position = new Board.Position(grid);
        System.out.println(position);
        System.out.println("White pieces:");
        java.util.HashSet<Cell> pl = position.getPieceList(Color.WHITE);
        for (Cell cell : pl) {
            System.out.print("* " + cell + ": ");
            java.util.HashSet<Move> hs = position.getLegalMoves(cell);
            for (Move move : hs) {
                System.out.print(move + ", ");
            }
            System.out.println();
        }
        System.out.println("Black pieces:");
        pl = position.getPieceList(Color.BLACK);
        for (Cell cell : pl) {
            System.out.print("* " + cell + ": ");
            java.util.HashSet<Move> hs = position.getLegalMoves(cell);
            for (Move move : hs) {
                System.out.print(move + ", ");
            }
            System.out.println();
        }
    }
}
