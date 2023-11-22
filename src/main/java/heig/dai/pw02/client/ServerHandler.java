package heig.dai.pw02.client;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.model.Message;
import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.PlayerColor;

import java.net.Socket;

public class ServerHandler {
    private final SocketManager socketManager;
    public ServerHandler(
            Socket playerConnection
    ) {
        this.socketManager = new SocketManager(playerConnection);
    }

    public PlayerColor receiveColor() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.COLOR) ? PlayerColor.valueOf(message.arguments()[0]) : null;
    }

    public void sendMove(int fromX, int fromY, int toX, int toY) {
        sendMessage(new Message(CCPMessage.MOVE, fromX + " " + fromY + " " + toX + " " + toY));
    }

    private void sendMessage(Message message) {
        socketManager.send(message);
    }

    private Message receiveMessage() {
        return socketManager.read();
    }
}
