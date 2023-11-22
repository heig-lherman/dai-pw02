package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPEntity;
import heig.dai.pw02.server.PlayerHandler;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.util.Assertions;

public final class ServerGameManager extends GameManager {

    private final PlayerPair players;
    private CCPEntity entity;
    private PlayerColor myColor;
    private PlayerColor playerTurn;

    public ServerGameManager(PlayerPair players) {
        this.players = players;
    }

    @Override
    public void start(ChessView view) {
        Assertions.assertNotNull(entity, "Entity cannot be null");
        super.start(view);
    }

    public void start(ChessView view, CCPEntity entity, PlayerColor color) {
        Assertions.assertNotNull(entity, "Entity cannot be null");
        this.entity = entity;
        if (entity == CCPEntity.CLIENT) {
            this.myColor = color;
        }
        this.playerTurn = color == PlayerColor.WHITE ? color : color.opposite();
        this.start(view);
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (entity == CCPEntity.CLIENT && (colorMoving != myColor || playerTurn() != colorMoving)) {
            return false;
        }
        boolean result = super.move(fromX, fromY, toX, toY);
        if (result) {
            switch (entity) {
                case SERVER:
                    System.out.println(colorMoving + " has moved");
                    System.out.println("Move sent to player " + playerTurn());
                    System.out.println("Waiting for player " + playerTurn() + " to move");
                    break;
                case CLIENT:
                    System.out.println("Move sent to server");
                    break;
            }
            return true;
        }else {
            if (entity == CCPEntity.SERVER) {
                System.out.println("Invalid move");
            }
            return false;
        }
    }

    @Override
    protected PlayerColor playerTurn() {
        return playerTurn;
    }

    @Override
    protected void updatePlayerTurn() {
        playerTurn = playerTurn.opposite();
    }
}
