package heig.dai.pw02.model;

import heig.dai.pw02.ccp.CCPMessage;
import heig.poo.chess.engine.util.Assertions;
import lombok.Getter;

import java.util.Arrays;

@Getter
public final class Message {

    private static final String DELIMITER = " ";

    private final CCPMessage type;
    private final String[] arguments;

    private Message(CCPMessage type, String... arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(
                type,
                String.join(DELIMITER, arguments)
        );
    }

    /**
     * Helper method that returns the arguments parsed from String to Integer
     * @return the arguments of the message parsed as integers
     */
    public int[] getNumericArguments() {
        return Arrays.stream(arguments).mapToInt(Integer::parseInt).toArray();
    }

    public static Message parse(String socketMessage) {
        String[] parts = socketMessage.split(DELIMITER);
        CCPMessage type = CCPMessage.valueOf(parts[0]);
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);
        return new Message(type, arguments);
    }

    public static Message of(CCPMessage type, String... arguments) {
        Assertions.assertTrue(type.nbrArguments() == arguments.length, "Wrong number of arguments");
        return new Message(type, arguments);
    }

    public static Message of(CCPMessage type, int... arguments) {
        String[] stringArguments = Arrays.stream(arguments).mapToObj(String::valueOf).toArray(String[]::new);
        return of(type, stringArguments);
    }
}
