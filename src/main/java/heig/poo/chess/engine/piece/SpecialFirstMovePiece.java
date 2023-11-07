package heig.poo.chess.engine.piece;

import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.util.Direction;
import heig.poo.chess.engine.util.Point;

/**
 * Class representing pieces having a first move that can be different from the others.
 * @author Vicky Butty
 * @author Loïc Herman
 * @author Massimo Stefani
 * @version 1.0
 */
public abstract class SpecialFirstMovePiece extends ChessPiece {

    private boolean hasMoved = false;

    protected SpecialFirstMovePiece(
            PlayerColor color,
            int posX,
            int posY,
            PieceType pieceType,
            Direction... possibleMoves
    ) {
        super(color, posX, posY, pieceType, possibleMoves);
    }

    /**
     * Method ti check if the piece has moved or not.
     *
     * @return true if the piece has moved, false otherwise
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Method used to update the piece position and set the hasMoved flag to true.
     *
     * @param pos the new position
     */
    @Override
    public void move(Point pos) {
        if (!hasMoved) {
            hasMoved = true;
        }

        super.move(pos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SpecialFirstMovePiece that = (SpecialFirstMovePiece) o;
        return hasMoved == that.hasMoved;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (hasMoved ? 1 : 0);
        return result;
    }
}
