package heig.dai.pw02.ccp;

import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.PieceType;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Board;
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
            if (message.getType().equals(CCPMessage.ERROR)) {
                return message;
            } else {
                return checkMessage(message, type);

            }
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
     *
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
        sendMessage(Message.of(CCPMessage.REPLAY, replay));
    }

    public Message createErrorMessage(CCPError error) {
        Message toReturn = Message.of(CCPMessage.ERROR, error.ordinal());
        log.error("{} - {}", toReturn, error.toString());
        return toReturn;
    }

    /**
     * First level of verification of the message. Check if the type is the expected one and if the number of arguments
     * is the expected one. The
     *
     * @param message the message to check
     * @param type    the expected type
     * @return true if the message is valid, false otherwise
     */
    private Message checkMessage(Message message, CCPMessage type) {
        CCPMessage messageType = message.getType();
        String[] argumentsString = message.getArguments();
        if (!messageType.equals(type)) {
            return createErrorMessage(CCPError.INVALID_MESSAGE);
        }

        if (argumentsString.length != type.nbrArguments()) {
            return createErrorMessage(CCPError.INVALID_NBR_ARGUMENTS);
        }

        if (messageType.equals(CCPMessage.PROMOTION)) {
            int[] arguments = message.getNumericArguments();
            if (!PieceType.values()[arguments[0]].equals(PieceType.QUEEN)
                    && !PieceType.values()[arguments[0]].equals(PieceType.ROOK)
                    && !PieceType.values()[arguments[0]].equals(PieceType.BISHOP)
                    && !PieceType.values()[arguments[0]].equals(PieceType.KNIGHT)) {
                return createErrorMessage(CCPError.INVALID_PROMOTION);
            }
        }

        if (messageType.equals(CCPMessage.REPLAY)
                && !(argumentsString[0].equals(ChessString.YES) || argumentsString[0].equals(ChessString.NO))) {
            return createErrorMessage(CCPError.INVALID_REPLAY);
        }

        if (messageType.equals(CCPMessage.MOVE)) {
            int[] arguments = message.getNumericArguments();
            for (int argument : arguments) {
                if (argument < 0 || argument > Board.BOARD_SIZE - 1) {
                    return createErrorMessage(CCPError.INVALID_MOVE);
                }
            }
            if (arguments[0] == arguments[2] && arguments[1] == arguments[3]) {
                return createErrorMessage(CCPError.INVALID_MOVE);
            }
        }

        return message;
    }
}
