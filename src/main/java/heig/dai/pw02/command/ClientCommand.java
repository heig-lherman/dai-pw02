package heig.dai.pw02.command;

import java.util.concurrent.Callable;

import heig.dai.pw02.ccp.CCPEntity;
import heig.dai.pw02.ccp.OnlineGameManager;
import heig.dai.pw02.socket.SocketManager;
import heig.poo.chess.ChessController;
import heig.poo.chess.ChessView;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.views.console.ConsoleView;
import heig.poo.chess.views.gui.GUIView;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
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
    private SocketManager socketManager;

    @Override
    public Integer call() {
        // Create socketManager, get color
        OnlineGameManager controller = new OnlineGameManager();
        ChessView view = useGui ? new GUIView(controller) : new ConsoleView(controller);
        controller.start(view, CCPEntity.CLIENT, PlayerColor.WHITE);
        return 0;
    }
}
