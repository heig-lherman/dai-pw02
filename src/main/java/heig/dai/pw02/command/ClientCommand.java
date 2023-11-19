package heig.dai.pw02.command;

import java.util.concurrent.Callable;

import heig.dai.pw02.socket.SocketManager;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
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
public class ClientCommand implements Callable<Integer> {

    @Option(
            names = {"-H", "--host"},
            description = "server host IP address",
            required = true
    )
    private String ipAddress;

    @Option(
            names = {"-p", "--port"},
            description = "server port",
            required = true
    )
    private int port;

    @Option(
            names = {"-g", "--gui"},
            description = "enable the use of a GUI to play the game"
    )
    private boolean useGui;
    private SocketManager socketManager;

    @Override
    public Integer call() throws Exception {
        log.trace("Starting client");
        log.trace("Connecting to {}:{}", ipAddress, port);
        socketManager = new SocketManager(ipAddress, port);
        return 0;
    }
}
