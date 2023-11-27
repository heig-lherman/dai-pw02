package heig.dai.pw02.command;

import heig.dai.pw02.client.ClientGameManager;
import heig.dai.pw02.client.ServerHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command to start a client to play chess
 *
 * @author Loïc Herman
 * @author Massimo Steffani
 */
@Slf4j
@Command(
        name = "client",
        description = "Start a client to play chess"
)
public class ClientCommand implements Runnable {

    @Option(
            names = {"-H", "--host"},
            description = "server host IP address",
            defaultValue = "127.0.0.1"
    )
    private String ipAddress;

    @Option(
            names = {"-p", "--port"},
            description = "server port",
            defaultValue = "6343"
    )
    private int port;

    @Override
    public void run() {
        Socket socket = openSocket(ipAddress, port);
        log.info("Connected to server, waiting for game to start...");
        new ClientGameManager(new ServerHandler(socket)).start();
    }

    private Socket openSocket(String server, int port) {
        try {
            return new Socket(server, port);
        } catch (IOException e) {
            log.error("Error while connecting to the server", e);
            throw new UncheckedIOException(e);
        }
    }
}
