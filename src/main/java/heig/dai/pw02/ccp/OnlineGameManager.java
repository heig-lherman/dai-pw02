package heig.dai.pw02.ccp;

import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.util.Assertions;

import java.util.List;

public class OnlineGameManager extends GameManager {
    private List<PlayerColor> players;
    private CCPEntity entity;

    @Override
    public void start(ChessView view) {
        Assertions.assertNotNull(entity, "Entity cannot be null");
        super.start(view);
    }

    public void start(ChessView view, CCPEntity entity) {
        Assertions.assertNotNull(entity, "Entity cannot be null");
        this.entity = entity;
        this.start(view);
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        boolean result = super.move(fromX, fromY, toX, toY);
        if (result) {
            switch (entity) {
                case SERVER:
                    // Envoi du message MOVE
                    updatePlayerTurn();
                    break;
                case CLIENT:
                    // Envoi du message MOVE
                    break;
            }
            return true;
        }else {
            if (entity == CCPEntity.SERVER) {
                // Envoi du message ERROR
            }
        }
        // Move pas valide, ressayer
        return false;
    }

    @Override
    protected PlayerColor playerTurn() {
        if (entity == CCPEntity.SERVER) {
            return super.playerTurn();
        }
        if (entity == CCPEntity.CLIENT) {
            // Ask server for player turn
        }
        return null;
    }

    @Override
    protected void updatePlayerTurn() {
        if (entity == CCPEntity.SERVER) {
            super.updatePlayerTurn();
            // Envoi du message YOURTURN
        }
    }
}
