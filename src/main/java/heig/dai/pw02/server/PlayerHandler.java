package heig.dai.pw02.server;

import heig.dai.pw02.model.Message;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerHandler implements Runnable {

    private final Socket playerConnection;
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public PlayerHandler(
            Socket playerConnection
    ) {
        this.playerConnection = playerConnection;
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
