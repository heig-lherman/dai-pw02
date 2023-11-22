package heig.dai.pw02.server;

import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.views.console.ConsoleView;
import heig.poo.chess.views.gui.GUIView;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ServerGamePool {

    private final ExecutorService threadPool;
    private final Queue<PlayerHandler> playerQueue = new ConcurrentLinkedQueue<>();

    public ServerGamePool(int maxGames) {
        // Create a thread pool to handle incoming connections from players.
        threadPool = new ThreadPoolExecutor(
                2, 2 * maxGames,
                10, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new PlayerThreadFactory()
        );
    }

    public synchronized CompletableFuture<?> handleIncomingPlayer(Socket playerConnection) {
        PlayerHandler playerHandler = new PlayerHandler(playerConnection);
        playerQueue.add(playerHandler);
        if (playerQueue.size() >= 1) {
            var pair = new PlayerPair(
                    playerQueue.poll(),
                    playerQueue.poll()
            );
            pair.sendColors();
            ServerGameManager gameManager = new ServerGameManager(pair);
            ChessView view = new ConsoleView(gameManager);
            gameManager.start(view);
        }

        return CompletableFuture.runAsync(playerHandler, threadPool);
    }

    private static class PlayerThreadFactory implements ThreadFactory {

        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "player-%d".formatted(counter.getAndIncrement()));
        }
    }
}
