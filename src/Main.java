import board.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Board board = new Board(new Board.Position("RNBQKBNR"));
        while (true) {
            System.out.println(board);
            String a = in.next();
            String b = in.next();
            board.makeMove(new Move(new Cell(a), new Cell(b)));
        }
    }
}
