package heig.dai.pw02.client;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.ccp.CCPHandler;
import heig.dai.pw02.model.Message;
import heig.poo.chess.PlayerColor;

import java.net.Socket;

public class ServerHandler extends CCPHandler {
    public ServerHandler(Socket playerConnection) {
        super(playerConnection);
    }

    public PlayerColor receiveColor() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.COLOR) ? PlayerColor.valueOf(message.arguments()) : null;
    }

    public void addReplayToStack(String replay) {
        super.addToStack(new Message(CCPMessage.REPLAY, replay));
    }

}
