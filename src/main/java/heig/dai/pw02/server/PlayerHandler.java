package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.model.Message;
import heig.dai.pw02.socket.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerHandler implements Runnable {

    private final SocketManager socketManager;
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public PlayerHandler(
            Socket playerConnection
    ) {
        this.socketManager = new SocketManager(playerConnection);
    }

    public void sendMessage(Message message) {
        socketManager.send(message);
    }

    public Message receiveMessage() {
        return socketManager.read();
    }

    public Message receiveMove() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.MOVE) ? message : null;
    }

    public void sendMove(int fromX, int fromY, int toX, int toY) {
        sendMessage(new Message(CCPMessage.MOVE, fromX + " " + fromY + " " + toX + " " + toY));
    }

    @Override
    public void run() {
        while (isRunning()) {
            // handle logic here
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
