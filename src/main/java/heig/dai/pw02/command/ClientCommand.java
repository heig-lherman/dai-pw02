package heig.dai.pw02.command;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.swing.text.html.parser.Entity;

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

    @Override
    public Integer call() {
        return 0;
    }
}
