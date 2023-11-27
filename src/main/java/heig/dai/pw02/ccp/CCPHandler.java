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
    private final Stack<Message<String>> messageStack = new Stack<>();

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

    public Message<String> receiveMove() {
        return receiveMessage(CCPMessage.MOVE);
    }

    public Message<String> receivePromotion() {
        return receiveMessage(CCPMessage.PROMOTION);
    }

    public Message<String> receiveReplay() {
        return receiveMessage(CCPMessage.REPLAY);
    }

    public void addMoveToStack(int fromX, int fromY, int toX, int toY) {
        addToStack(Message.withParsedArgsFromIntToString(new Message<>(CCPMessage.MOVE, fromX, fromY, toX, toY)));
    }

    public void addPromotionToStack(ChessPiece piece) {
        addToStack(Message.withParsedArgsFromIntToString(
                new Message<>(CCPMessage.PROMOTION, piece.getPieceType().ordinal(), piece.getX(), piece.getY())));
    }

    public void addReplayToStack(String replay) {
        Assertions.assertTrue(replay.equals("Yes") || replay.equals("No"), "Replay must be Yes or No");
        addToStack(new Message<>(CCPMessage.REPLAY, replay));
    }

    protected void addToStack(Message<String> message) {
        messageStack.add(message);
    }

    protected void sendMessage(Message<String> message) {
        socketManager.send(message);
    }

    protected Message<String> receiveMessage() {
        return socketManager.read();
    }

    protected Message<String> receiveMessage(CCPMessage type) {
        Message<String> message = socketManager.read();
        if(message.getType().equals(type)) {
            return message;
        }else{
            // Send error message, type is not the expected one
            return null;
        }
    }

}
