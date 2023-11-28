package heig.dai.pw02.ccp;

import heig.dai.pw02.model.Message;
import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.PieceType;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;
import heig.poo.chess.engine.util.Board;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
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
        return checkMessage(message, type);
    }

    public Message createErrorMessage(CCPError error) {
        Message toReturn = Message.of(CCPMessage.ERROR, error.ordinal());
        log.error("{} - {}", toReturn, error.toString());
        return toReturn;
    }

    /**
     * First level of verification of the message. Check if the type is the expected one and if the number of arguments
     * is the expected one. The
     * @param message the message to check
     * @param type the expected type
     * @return true if the message is valid, false otherwise
     */
    private Message checkMessage(Message message, CCPMessage type) {
        CCPMessage messageType = message.getType();
        String[] argumentsString = message.getArguments();
        if(!messageType.equals(type)) {
            return createErrorMessage(CCPError.INVALID_MESSAGE);
        }
        if(argumentsString.length != type.nbrArguments()) {
            return createErrorMessage(CCPError.INVALID_NBR_ARGUMENTS);
        }
        if(messageType.equals(CCPMessage.PROMOTION)) {
            int[] arguments = message.getNumericArguments();
            if(!PieceType.values()[arguments[0]].equals(PieceType.QUEEN)
                    && !PieceType.values()[arguments[0]].equals(PieceType.ROOK)
                    && !PieceType.values()[arguments[0]].equals(PieceType.BISHOP)
                    && !PieceType.values()[arguments[0]].equals(PieceType.KNIGHT)){
                return createErrorMessage(CCPError.INVALID_PROMOTION);
            }
        }
        if(messageType.equals(CCPMessage.REPLAY)
                && !(argumentsString[0].equals("Yes") || argumentsString[0].equals("No"))) {
            return createErrorMessage(CCPError.INVALID_REPLAY);
        }
        if(messageType.equals(CCPMessage.MOVE)) {
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
