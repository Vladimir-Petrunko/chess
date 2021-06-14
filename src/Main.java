import board.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);
        Scanner in = new Scanner(System.in);
        while (true) {
            String a = in.next();
            String b = in.next();
            Cell ca = Cell.from(a);
            Cell cb = Cell.from(b);
            System.out.println(board.isLegalMove(ca, cb, null));
        }
    }
}
