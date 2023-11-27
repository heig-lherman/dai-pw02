package heig.dai.pw02.ccp;

import heig.dai.pw02.model.Message;
import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CCPHandler {

    private final SocketManager socketManager;
    private final Queue<Message> messageStack = new ConcurrentLinkedQueue<>();

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

    public void sendStack() {
        while (!messageStack.isEmpty()) {
            sendMessage(messageStack.poll());
        }
    }

    public Message receiveMove() {
        return receiveMessage(CCPMessage.MOVE);
    }

    public Message receivePromotion() {
        return receiveMessage(CCPMessage.PROMOTION);
    }

    public Message receiveReplay() {
        return receiveMessage(CCPMessage.REPLAY);
    }

    public void addMoveToStack(int fromX, int fromY, int toX, int toY) {
        addToStack(Message.of(CCPMessage.MOVE, fromX, fromY, toX, toY));
    }

    public void addPromotionToStack(ChessPiece piece) {
        addToStack(Message.of(CCPMessage.PROMOTION, piece.getPieceType().ordinal(), piece.getX(), piece.getY()));
    }

    public void addReplayToStack(String replay) {
        Assertions.assertTrue(replay.equals("Yes") || replay.equals("No"), "Replay must be Yes or No");
        addToStack(Message.of(CCPMessage.REPLAY, replay));
    }

    protected void addToStack(Message message) {
        messageStack.add(message);
    }

    protected void sendMessage(Message message) {
        socketManager.send(message);
    }

    protected Message receiveMessage(CCPMessage type) {
        Message message = socketManager.read();
        if (message.getType().equals(type)) {
            return message;
        }

        // TODO: Handle error
        return null;
    }
}
