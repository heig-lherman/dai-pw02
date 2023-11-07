package heig.poo.chess.engine.piece;

/**
 * Interface representing a piece that can be promoted.
 * @author Vicky Butty
 * @author Loïc Herman
 * @author Massimo Stefani
 * @version 1.0
 */
public interface PromotablePiece {

    /**
     * Method used to check if the piece can be promoted.
     *
     * @return true if the piece can be promoted, false otherwise.
     */
    boolean canPromote();
}
