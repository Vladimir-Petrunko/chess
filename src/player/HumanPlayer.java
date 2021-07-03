package player;

import board.Move;
import board.Cell;
import board.Board.Position;
import board.Color;
import board.MoveCategory;
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

    private void info(Position position) {
        out.println("White pieces:");
        java.util.HashSet<Cell> pl = position.getPieceList(Color.WHITE);
        for (Cell cell : pl) {
            out.print("* " + cell + ": ");
            java.util.HashSet<Move> hs = position.getLegalMoves(cell);
            for (Move move : hs) {
                out.print(move + ", ");
            }
            out.println();
        }
        out.println("Black pieces:");
        pl = position.getPieceList(Color.BLACK);
        for (Cell cell : pl) {
            out.print("* " + cell + ": ");
            java.util.HashSet<Move> hs = position.getLegalMoves(cell);
            for (Move move : hs) {
                out.print(move + ", ");
            }
            out.println();
        }
    }

    @Override
    public Move makeMove(Position position, Color color) {
        Move move = null;
        while (move == null) {
            info(position);
            out.println(nickname + ", you are playing with the " + color.toString() + " pieces.");
            out.println("Here is your position:");
            out.println(position);
            out.println("Please enter your move in the following format: x0-y0");
            String str = in.nextLine();
            if (str.equals("0-0") || str.equals("0-0-0")) {
                // Possible castling
                Cell kingCell = position.getKingCell(color);
                Piece king = position.get(kingCell);
                int delta = str.equals("0-0") ? CASTLING_DELTA : -CASTLING_DELTA;
                Cell landingCell = kingCell.shift(0, delta);
                move = new Move(kingCell, landingCell, king);
            } if (!(str.length() == 5 || str.length() == 6) || str.charAt(2) != '-') {
                out.println("Unable to parse move " + str + ".");
                continue;
            } else {
                Cell initial = new Cell(str.substring(0, 2));
                Cell target = new Cell(str.substring(3, 5));
                Piece piece = position.get(initial);
                if (str.length() == 5) {
                    move = new Move(initial, target, piece);
                } else {
                    MoveCategory category = MoveCategory.ORDINARY;
                    if (str.endsWith("Q")) {
                        category = MoveCategory.PROMOTE_TO_QUEEN;
                    } else if (str.endsWith("R")) {
                        category = MoveCategory.PROMOTE_TO_ROOK;
                    } else if (str.endsWith("B")) {
                        category = MoveCategory.PROMOTE_TO_BISHOP;
                    } else if (str.endsWith("N")) {
                        category = MoveCategory.PROMOTE_TO_KNIGHT;
                    }
                    move = new Move(initial, target, piece, category);
                }
            }
            if (!position.isLegalMove(move) || position.getColor(move.getStart()) != color) {
                String moveString = (move.getPiece() == null ? ("?" + str) : move.toString());
                out.println("Sorry, the move " + moveString + " is not a legal move in the current position.");
                move = null;
            }
        }
        return move;
    }
}
