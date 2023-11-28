package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPHandler;
import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.ccp.Message;
import heig.poo.chess.PlayerColor;

import java.net.Socket;

public final class PlayerHandler extends CCPHandler {

    public PlayerHandler(Socket playerConnection) {
        super(playerConnection);
    }

    public void sendColor(PlayerColor color) {
        sendMessage(Message.of(CCPMessage.COLOR, color.name()));
    }
}