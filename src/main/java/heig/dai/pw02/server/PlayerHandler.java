package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.ccp.CCPHandler;
import heig.dai.pw02.model.Message;
import heig.poo.chess.PlayerColor;

import java.net.Socket;

public class PlayerHandler extends CCPHandler implements Runnable {
    public PlayerHandler(Socket playerConnection) {
        super(playerConnection);
    }

    public void sendColor(PlayerColor color) {
        sendMessage(new Message<>(CCPMessage.COLOR, color.toString()));
    }

    @Override
    public void run() {

    }
}
