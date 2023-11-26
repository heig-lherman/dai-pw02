package heig.dai.pw02.ccp;

import heig.dai.pw02.model.Message;
import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;

import java.io.IOException;
import java.net.Socket;
import java.util.Stack;

public abstract class CCPHandler {
    private final SocketManager socketManager;
    private final Stack<Message> messageStack = new Stack<>();

    public CCPHandler(Socket socketManager) {
        this.socketManager = new SocketManager(socketManager);
    }

    public void disconnect() {
        try {
            socketManager.closeSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendStack() {
        while (!messageStack.isEmpty()) {
            sendMessage(messageStack.pop());
        }
    }

    public Message receiveMove() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.MOVE) ? message : null;
    }

    public void addMoveToStack(int fromX, int fromY, int toX, int toY) {
        messageStack.add(new Message(CCPMessage.MOVE, fromX + " " + fromY + " " + toX + " " + toY));
    }

    public void addPromotionToStack(ChessPiece piece) {
        addToStack(new Message(CCPMessage.PROMOTION,
                piece.getPieceType().toString() + " " + piece.getX() + " " + piece.getY()));
    }

    public Message receivePromotion() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.PROMOTION) ? message : null;
    }

    public Message receiveReplay() {
        Message message = receiveMessage();
        return message.type().equals(CCPMessage.REPLAY) ? message : null;
    }

    public void addReplayToStack(String replay) {
        Assertions.assertTrue(replay.equals("Yes") || replay.equals("No"), "Replay must be Yes or No");
        addToStack(new Message(CCPMessage.REPLAY, replay));
    }

    protected void addToStack(Message message) {
        messageStack.add(message);
    }

    protected void sendMessage(Message message) {
        socketManager.send(message);
    }

    protected Message receiveMessage() {
        return socketManager.read();
    }

}
