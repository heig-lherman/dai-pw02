package heig.dai.pw02.client;

import heig.dai.pw02.ccp.CCPHandler;
import heig.dai.pw02.ccp.CCPMessage;
import heig.poo.chess.PlayerColor;

import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class ServerHandler extends CCPHandler {

    public ServerHandler(Socket playerConnection) {
        super(playerConnection);
    }

    public CompletableFuture<PlayerColor> awaitColor() {
        return awaitMessage(CCPMessage.COLOR).thenApply(
                message -> PlayerColor.valueOf(message.getArguments()[0])
        );
    }
}