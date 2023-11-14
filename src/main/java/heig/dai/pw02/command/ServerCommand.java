package heig.dai.pw02.command;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
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
            required = true
    )
    private int port;

    @Override
    public Integer call() throws Exception {
        log.trace("Starting server");
        log.trace("Opening pool listening on port {}", port);
        return 0;
    }
}
