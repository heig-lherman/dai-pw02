package heig.dai.pw02.server;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The server game poll handles incoming player connections and creates games.
 */
public final class ServerGamePool {

    private final Queue<PlayerHandler> playerQueue = new ConcurrentLinkedQueue<>();

    public ServerGamePool() {
    }

    /**
     * Handle an incoming player connection.
     *
     * @param playerConnection The player connection.
     */
    public synchronized void handleIncomingPlayer(Socket playerConnection) {
        PlayerHandler playerHandler = new PlayerHandler(playerConnection);
        playerQueue.add(playerHandler);
        if (playerQueue.size() >= 2) {
            var pair = new PlayerPair(playerQueue.poll(), playerQueue.poll());
            var gameManager = new ServerGameManager(pair);
            gameManager.start();
        }
    }
}
