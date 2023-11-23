package heig.dai.pw02.command;

import java.net.Socket;
import java.util.concurrent.Callable;

import heig.dai.pw02.client.ClientGameManager;
import heig.dai.pw02.client.ServerHandler;
import heig.poo.chess.ChessView;
import heig.poo.chess.views.console.ConsoleView;
import heig.poo.chess.views.gui.GUIView;
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

    @Option(
            names = {"-g", "--gui"},
            description = "enable the use of a GUI to play the game"
    )
    private boolean useGui;

    @Override
    public Integer call() {
        Socket socket = null;
        try{
            socket = new Socket(ipAddress, port);
        } catch (Exception e){

        }
        ServerHandler player = new ServerHandler(socket);
        ClientGameManager controller = new ClientGameManager();
        ChessView view = useGui ? new GUIView(controller, "Client") : new ConsoleView(controller);
        controller.start(view, player);
        return 0;
    }
}
