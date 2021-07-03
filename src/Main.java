import board.*;
import player.*;
import utils.*;

public class Main {

    public static void main(String[] args) {
        Player white = new RandomPlayer("Polina");
        Player black = new RandomPlayer("Natasha");
        Game game = new Game(white, black);
        Status status = game.play();
        System.out.println(game.getGameNotation());
        System.out.println(game.getFinalPosition());
    }
}
