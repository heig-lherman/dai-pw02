package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPEntity;
import heig.dai.pw02.server.PlayerHandler;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.util.Assertions;

public final class ServerGameManager extends GameManager {

    private final PlayerPair players;

    public ServerGameManager(PlayerPair players) {
        this.players = players;
    }

    @Override
    public void start(ChessView view) {
        super.start(view);
        listenToPlayers();
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (super.move(fromX, fromY, toX, toY)) {
            System.out.println(colorMoving + " has moved");
            System.out.println("Move sent to player " + playerTurn());
            System.out.println("Waiting for player " + playerTurn() + " to move");
            return true;
        }else {
            System.out.println("Invalid move");
            return false;
        }
    }

    private void listenToPlayers() {
        while (player.isRunning()) {
            CCPEntity message = player.receiveMessage();
            if (message.type().equals(CCPMessage.MOVE)) {
                String[] arguments = message.arguments();
                int fromX = Integer.parseInt(arguments[0]);
                int fromY = Integer.parseInt(arguments[1]);
                int toX = Integer.parseInt(arguments[2]);
                int toY = Integer.parseInt(arguments[3]);
                move(fromX, fromY, toX, toY);
            }
        }
    }

}
