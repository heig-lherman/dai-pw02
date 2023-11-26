package heig.dai.pw02.command;

import java.net.Socket;
import java.util.concurrent.Callable;

import heig.dai.pw02.client.ClientGameManager;
import heig.dai.pw02.client.ServerHandler;
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

    @Override
    public Integer call() {
        Socket socket = null;
        try{
            socket = new Socket(ipAddress, port);
        } catch (Exception e){
            System.out.println("Error while connecting to the server");
            System.exit(0);
        }
        new ClientGameManager(new ServerHandler(socket)).start();
        return 0;
    }
}
