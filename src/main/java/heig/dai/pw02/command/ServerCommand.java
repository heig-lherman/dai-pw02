package heig.dai.pw02.command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;


import heig.dai.pw02.server.ServerGamePool;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command to start a server to host chess games
 *
 * @author Loïc Herman
 * @author Massimo Steffani
 */
@Slf4j
@Command(
        name = "server",
        description = "Start a server to host chess games"
)
public class ServerCommand implements Callable<Integer> {
    private final static int MAX_GAMES = 1;

    @Option(
            names = {"-p", "--port"},
            description = "server port",
            required = true
    )
    private int port;

    @Override
    public Integer call() throws Exception {
        ServerGamePool pool = new ServerGamePool(MAX_GAMES);
        Socket clientSocket;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    pool.handleIncomingPlayer(clientSocket);
                } catch (IOException e) {
                    log.error("Error while accepting connection", e);
                }
            }
        } catch (IOException e) {
            log.error("Error while creating server socket", e);
            return 1;
        }
    }
}
