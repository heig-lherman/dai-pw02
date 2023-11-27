package heig.dai.pw02.ccp;

import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;
import heig.poo.chess.engine.util.ChessString;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CCPHandler {

    private final SocketManager socketManager;

    // We need this to put promotions on hold until we send the move message
    private final AtomicReference<Message> pendingPromotion = new AtomicReference<>();

    public CCPHandler(Socket socketManager) {
        this.socketManager = new SocketManager(socketManager);
    }

    public void disconnect() {
        try {
            socketManager.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void sendMessage(Message message) {
        log.trace("Sending message of type {}", message.getType());
        socketManager.send(message);
    }

    protected CompletableFuture<Message> awaitMessage(CCPMessage type) {
        log.trace("Waiting for message of type {}", type);
        return CompletableFuture.supplyAsync(() -> {
            Message message = socketManager.read();
            if (message == null) {
                log.warn("A client got disconnected");
                System.exit(2);
            }

            if (message.getType() == type) {
                return message;
            }

            log.warn("Received message of type {} instead of {}", message.getType(), type);
            return null;
        });
    }

    public final CompletableFuture<Message> awaitMove() {
        return awaitMessage(CCPMessage.MOVE);
    }

    public final CompletableFuture<Message> awaitPromotion() {
        return awaitMessage(CCPMessage.PROMOTION);
    }

    public final CompletableFuture<Message> awaitReplay() {
        return awaitMessage(CCPMessage.REPLAY);
    }

    public final void sendMove(int fromX, int fromY, int toX, int toY) {
        sendMessage(Message.of(CCPMessage.MOVE, fromX, fromY, toX, toY));
        if (pendingPromotion.get() != null) {
            sendMessage(pendingPromotion.get());
            pendingPromotion.set(null);
        }
    }

    /**
     * Send a promotion to the other player, but only once the move has been sent.
     * @param piece the piece to promote to
     */
    public final void sendPromotion(ChessPiece piece) {
        pendingPromotion.set(Message.of(
                CCPMessage.PROMOTION,
                piece.getPieceType().ordinal(),
                piece.getX(), piece.getY()
        ));
    }

    public final void sendReplay(String replay) {
        Assertions.assertTrue(
                replay.equals(ChessString.YES) || replay.equals(ChessString.NO),
                "Replay must be Yes or No"
        );
        sendMessage(Message.of(CCPMessage.REPLAY, replay));
    }
}
