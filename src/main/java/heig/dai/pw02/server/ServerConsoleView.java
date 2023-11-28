package heig.dai.pw02.server;

import heig.poo.chess.ChessController;
import heig.poo.chess.views.console.ConsoleView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerConsoleView extends ConsoleView {

    public ServerConsoleView(ChessController controller) {
        super(controller);
    }

    @Override
    public void startView() {
        log.info("Starting game...");
        controller.newGame();
    }

    @Override
    public void printBoard() {
        super.printBoard();
    }
}
