package board;

public enum MoveCategory {
    // TODO: add full support of en passant (maybe not in MoveCategory, but somewhere...)
    // Should be self-explanatory enough
    UNCATEGORIZED,
    ORDINARY,
    INVALID,
    O_O,
    O_O_O,
    PROMOTE_TO_QUEEN,
    PROMOTE_TO_ROOK,
    PROMOTE_TO_BISHOP,
    PROMOTE_TO_KNIGHT
}