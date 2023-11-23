package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPEntity;
import heig.dai.pw02.model.Message;
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

    private void listenToPlayer() {
        while (true) {
            PlayerHandler player = players.get(playerTurn());
            Message message = player.receiveMove();
            String[] splitedArgs = message.arguments().split(" ");
            remoteMove(
                    Integer.parseInt(splitedArgs[0]),
                    Integer.parseInt(splitedArgs[1]),
                    Integer.parseInt(splitedArgs[2]),
                    Integer.parseInt(splitedArgs[3])
            );
            PlayerHandler otherPlayer = players.get(playerTurn());
            otherPlayer.sendMove(
                    Integer.parseInt(splitedArgs[0]),
                    Integer.parseInt(splitedArgs[1]),
                    Integer.parseInt(splitedArgs[2]),
                    Integer.parseInt(splitedArgs[3]));
        }
    }

    @Override
    public void start(ChessView view) {
        super.start(view);
        listenToPlayer();
    }

    public boolean remoteMove(int fromX, int fromY, int toX, int toY) {
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

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        return false;
    }
}
