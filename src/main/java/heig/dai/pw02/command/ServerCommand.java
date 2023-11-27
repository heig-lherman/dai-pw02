package heig.dai.pw02.command;

import heig.dai.pw02.server.ServerGamePool;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
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

    @Option(
            names = {"-p", "--port"},
            description = "server port",
            defaultValue = "6343"
    )
    private int port;

    @Override
    public Integer call() {
        ServerGamePool pool = new ServerGamePool();
        log.info("Starting server on port {}", port);
        try(var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    var clientSocket = serverSocket.accept();
                    log.info("New connection from {}", clientSocket.getInetAddress());
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
