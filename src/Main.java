import board.*;
import player.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(new HumanPlayer(), new HumanPlayer());
        game.play();
    }
}
