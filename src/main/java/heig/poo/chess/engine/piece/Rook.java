package heig.poo.chess.engine.piece;

import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.util.Board;
import heig.poo.chess.engine.util.Direction;

/**
 * Class representing a Rook in a chess game.
 * @author Vicky Butty
 * @author Loïc Herman
 * @author Massimo Stefani
 * @version 1.0
 */
public final class Rook extends SpecialFirstMovePiece implements CastlingPiece {

    private static final PieceType PIECE_TYPE = PieceType.ROOK;
    private static final int MAX_LENGTH_MOVE = Board.BOARD_SIZE - 1;
    private static final Direction[] POSSIBLE_MOVES = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    /**
     * Creates a new Rook with the given color and position.
     *
     * @param color the color of the rook
     * @param posX  the x position
     * @param posY  the y position
     */
    public Rook(PlayerColor color, int posX, int posY) {
        super(color, posX, posY, PIECE_TYPE, POSSIBLE_MOVES);
    }

    @Override
    protected int possibleLengthDistance() {
        return MAX_LENGTH_MOVE;
    }

    @Override
    public boolean canCastle() {
        return !hasMoved();
    }

}
