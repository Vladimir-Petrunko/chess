import board.*;
import player.*;
import utils.*;

public class Main {

    public static void main(String[] args) {
        Player white = new RandomPlayer("Polina");
        Player black = new RandomPlayer("Natasha");
        int w = 0;
        int d = 0;
        int b = 0;
        for (int i = 0; i < 100; i++) {
            Game game = new Game(white, black);
            Status status = game.play();
            if (status == Status.WHITE_WON) w++;
            if (status == Status.BLACK_WON) b++;
            if (status == Status.DRAW) d++;
        }
        System.out.println("White won: " + w);
        System.out.println("Black won: " + b);
        System.out.println("Draw: " + d);
    }
}
