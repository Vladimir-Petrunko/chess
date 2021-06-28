package player;

import board.Move;
import board.Cell;
import board.Board.Position;
import board.Color;
import pieces.Piece;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static utils.Global.CASTLING_DELTA;
import static utils.Global.SIZE;

public class HumanPlayer implements Player {
    private final String nickname;
    private final Scanner in;
    private final PrintStream out;

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
                int delta = str.equals("0-0") ? CASTLING_DELTA : -CASTLING_DELTA;
                Cell landingCell = kingCell.shift(0, delta);
                move = new Move(kingCell, landingCell, king);
            } else if (str.length() != 5 || str.charAt(2) != '-') {
                out.println("Unable to parse move " + str + ".");
                continue;
            } else {
                try {
                    Cell initial = new Cell(str.substring(0, 2));
                    Cell target = new Cell(str.substring(3, 5));
                    Piece piece = position.get(initial);
                    move = new Move(initial, target, piece);
                } catch (IllegalArgumentException ex) {
                    out.println(ex.getMessage());
                }
            }
            if (move == null || !position.isLegalMove(move) || position.getColor(move.getStart()) != color) {
                String moveString = ((move == null || move.getPiece() == null) ? ("?" + str) : move.toString());
                out.println("Sorry, the move " + moveString + " is not a legal move in the current position.");
                move = null;
            }
        }
        return move;
    }
}
