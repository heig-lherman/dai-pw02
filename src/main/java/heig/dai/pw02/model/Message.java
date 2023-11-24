package heig.dai.pw02.model;

import heig.dai.pw02.ccp.CCPMessage;

import java.util.List;

public record Message(CCPMessage type, String arguments) {


    @Override
    public String toString() {
        return switch (type) {
            case COLOR -> "COLOR " + arguments;
            case MOVE -> "MOVE " + arguments;
            case PROMOTION -> "PROMOTION " + arguments;
            case REPLAY -> "REPLAY " + arguments;
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

    public static Integer[] parseArgumentsToInt(Message message) {
        return List.of(message.arguments().split(" ")).stream().map(Integer::parseInt).toArray(Integer[]::new);
    }

    public static String[] parseArgumentsToString(Message message) {
        return message.arguments().split(" ");
    }
}
