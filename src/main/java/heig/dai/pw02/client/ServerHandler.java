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
        return PlayerColor.valueOf(receiveMessage(CCPMessage.COLOR).getArguments()[0]);
    }

}
