package heig.dai.pw02.client;

import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ClientGameManager extends GameManager {
    private ServerHandler server;
    private PlayerColor myColor;
    public void start(ChessView view, ServerHandler server) {
        Assertions.assertNotNull(server, "Player cannot be null");
        this.server = server;
        this.myColor = server.receiveColor();
        super.start(view);
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }

    private void listenMove() {
        Message message = server.receiveMove();
        Integer[] parsedArgs = Message.parseArgumentsToInt(message);
        remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        ChessPiece piece = super.board.getPiece(fromX, fromY);
        if (Objects.isNull(piece)) {
            return false;
        }
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (colorMoving != myColor || playerTurn() != colorMoving) {
            return false;
        }
        if (remoteMove(fromX, fromY, toX, toY)) {
            server.sendMove(fromX, fromY, toX, toY);
            Thread thread = new Thread(this::listenMove);
            thread.start();
            return true;
        }else {
            System.out.println("Invalid move");
            return false;
        }
    }

    private boolean remoteMove(int fromX, int fromY, int toX, int toY) {
        return super.move(fromX, fromY, toX, toY);
    }
}
