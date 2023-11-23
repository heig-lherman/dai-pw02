package heig.dai.pw02.client;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;

public class ClientGameManager extends GameManager {
    ServerHandler server;
    PlayerColor myColor;
    public void start(ChessView view, ServerHandler server) {
        Assertions.assertNotNull(server, "Player cannot be null");
        this.server = server;
        this.myColor = server.receiveColor();
        super.start(view);
        if (myColor == PlayerColor.BLACK) {
            listenToServer();
        }
    }

    private void listenToServer() {
        Message message = server.receiveMove();
        String[] splitedArgs = message.arguments().split(" ");
        remoteMove(
                Integer.parseInt(splitedArgs[0]),
                Integer.parseInt(splitedArgs[1]),
                Integer.parseInt(splitedArgs[2]),
                Integer.parseInt(splitedArgs[3])
        );
    }

    private boolean remoteMove(int fromX, int fromY, int toX, int toY) {
        return super.move(fromX, fromY, toX, toY);
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        ChessPiece piece = super.board.getPiece(fromX, fromY);
        if (piece == null) {
            return false;
        }
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (colorMoving != myColor || playerTurn() != colorMoving) {
            return false;
        }
        if (remoteMove(fromX, fromY, toX, toY)) {
            server.sendMove(fromX, fromY, toX, toY);
            Thread thread = new Thread(this::listenToServer);
            thread.start();
            return true;
        }else {
            System.out.println("Invalid move");
            return false;
        }
    }
}
