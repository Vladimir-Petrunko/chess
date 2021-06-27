package player;

import board.Move;
import board.Cell;
import board.Board.Position;
import board.Color;
import pieces.Piece;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final String nickname;
    private final Scanner in;
    private final PrintStream out;

    /**
     * Constructor of {@code HumanPlayer} from I/O streams provided as parameters.
     *
     * @param in an {@code InputStream}
     * @param out an {@code OutputStream}
     */
    public HumanPlayer(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
        out.println("Please enter your nickname:");
        this.nickname = this.in.nextLine();
    }

    /**
     * Default constructor of {@code HumanPlayer}. Uses {@code System.in} and {@code System.out} for user interaction.
     */
    public HumanPlayer() {
        this(System.in, System.out);
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Move makeMove(Position position, Color color) {
        Move move = null;
        while (move == null) {
            out.println(nickname + ", you are playing with the " + color.toString() + " pieces.");
            out.println("Here is your position:");
            out.println(position);
            out.println("Please enter your move in the following format: x0-y0");
            String str = in.nextLine();
            if (str.equals("0-0") || str.equals("0-0-0")) {
                Cell kingCell = position.getKingCell(color);
                Piece king = position.get(kingCell);
                int column = str.equals("0-0") ? position.getRightmostColumn(color) : position.getLeftmostColumn(color);
                Cell rookCell = new Cell(kingCell.getRow(), column);
                move = new Move(kingCell, rookCell, king);
            } else if (str.length() != 5 || str.charAt(2) != '-') {
                out.println("Unable to parse move " + str + ".");
            } else {
                try {
                    Cell initial = new Cell(str.substring(0, 2));
                    Cell target = new Cell(str.substring(3, 5));
                    move = new Move(initial, target, position.get(initial));
                    if (!position.isLegalMove(move) || position.getColor(initial) != color) {
                        move = null;
                    }
                } catch (IllegalArgumentException ex) {
                    out.println(ex.getMessage());
                }
            }
        }
        return move;
    }
}
