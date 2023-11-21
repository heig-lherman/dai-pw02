package heig.dai.pw02.model;

import heig.dai.pw02.ccp.CCPMessage;

public final class Message {

    private final CCPMessage type;
    private final String[] arguments;

    private Message(
            CCPMessage type,
            String[] arguments
    ) {
        this.type = type;
        this.arguments = arguments;
    }

    public static Message parse(String socketMessage) {
        return
    }
}
