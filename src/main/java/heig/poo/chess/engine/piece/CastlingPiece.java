package heig.poo.chess.engine.piece;

/**
 * Interface pieces that are involved in a castling move.
 * @author Vicky Butty
 * @author Loïc Herman
 * @author Massimo Stefani
 * @version 1.0
 */
public interface CastlingPiece {

    /**
     * Method used to check if the piece can castle.
     *
     * @return whether the piece can castle.
     */
    boolean canCastle();
}
