package heig.dai.pw02.client;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.util.Assertions;

public class ClientGameManager extends GameManager {
    ServerHandler server;
    PlayerColor myColor;
    public void start(ChessView view, ServerHandler server) {
        Assertions.assertNotNull(server, "Player cannot be null");
        this.server = server;
        this.myColor = server.receiveColor();
        super.start(view);
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (colorMoving != myColor || playerTurn() != colorMoving) {
            return false;
        }
        boolean result = super.move(fromX, fromY, toX, toY);
        if (result) {
            server.sendMove(fromX, fromY, toX, toY);
            return true;
        }else {
            System.out.println("Invalid move");
            return false;
        }
    }
}
