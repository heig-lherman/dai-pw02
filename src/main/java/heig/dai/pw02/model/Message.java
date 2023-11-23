package heig.dai.pw02.model;

import heig.dai.pw02.ccp.CCPMessage;

import java.util.List;

public record Message(CCPMessage type, String arguments) {

    @Override
    public String toString() {
        return switch (type) {
            case HELLO -> "HELLO";
            case COLOR -> "COLOR " + arguments;
            case OK -> "OK";
            case MOVE -> "MOVE " + arguments;
            case YOURTURN -> "YOURTURN";
            case STALEMATE -> "STALEMATE";
            case CHECKMATE -> "CHECKMATE";
            case NOMATERIAL -> "NOMATERIAL";
            case ERROR -> "ERROR";
            default -> throw new RuntimeException("Unknown message type");
        };
    }

    public static Message parse(String socketMessage) {
        String[] parts = socketMessage.split(" ");
        CCPMessage type = CCPMessage.valueOf(parts[0]);
        String arguments = socketMessage.substring(type.toString().length() + 1);
        return new Message(type, arguments);
    }
}
