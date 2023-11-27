package heig.dai.pw02.model;

import heig.dai.pw02.ccp.CCPMessage;
import heig.poo.chess.engine.util.Assertions;
import lombok.Getter;

import java.util.Arrays;

public class Message<E> {
    private static final String DELIMETER = " ";

    private final CCPMessage type;
    private final E[] arguments;

    @SuppressWarnings("unchecked")
    public Message(CCPMessage type, String ... arguments) {
        Assertions.assertTrue(type.nbrArguments() == arguments.length, "Wrong number of arguments");
        this.type = type;
        this.arguments = (E[]) Arrays.copyOf(arguments, arguments.length);
    }

    @SuppressWarnings("unchecked")
    public Message(CCPMessage type, Integer ... arguments) {
        Assertions.assertTrue(type.nbrArguments() == arguments.length, "Wrong number of arguments");
        this.type = type;
        this.arguments = (E[]) Arrays.copyOf(arguments, arguments.length);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.toString()).append(DELIMETER);
        Arrays.stream(arguments).forEach(s -> builder.append(s).append(DELIMETER));
        return builder.toString().trim();
    }

    public static Message<String> parse(String socketMessage) {
        String[] parts = socketMessage.split(DELIMETER);
        CCPMessage type = CCPMessage.valueOf(parts[0]);
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);
        return new Message<>(type, arguments);
    }

    public static Message<Integer> withParsedArgsFromStringToInt(Message<String> message) {
        Object[] arguments = message.getArguments();
        Integer[] intArguments = new Integer[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            intArguments[i] = Integer.parseInt((String) arguments[i]);
        }
        return new Message<>(message.getType(), intArguments);
    }

    public static Message<String> withParsedArgsFromIntToString(Message<Integer> message) {
        Object[] arguments = message.getArguments();
        String[] stringArguments = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            stringArguments[i] = String.valueOf(arguments[i]);
        }
        return new Message<>(message.getType(), stringArguments);
    }

    public CCPMessage getType() {
        return type;
    }

    public E[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}
