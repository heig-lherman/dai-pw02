package heig.dai.pw02.server;

import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.ChessView.UserChoice;
import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ServerGameManager extends GameManager {

    private final PlayerPair players;

    public ServerGameManager(PlayerPair players) {
        super();
        this.players = players;
        this.players.sendColors();
    }

    /**
     * Helper to start a game since this manager has a hard-coded view.
     */
    public void start() {
        start(null);
    }

    @Override
    public void start(ChessView view) {
        // the server console view is a very restrictive view that will never make moves
        super.start(new ServerConsoleView(this));
        listenToPlayer();
    }

    public void remoteMove(int fromX, int fromY, int toX, int toY) {
        PlayerColor colorMoving = board.getPiece(fromX, fromY).getPlayerColor();
        if (super.move(fromX, fromY, toX, toY)) {
            log.info("{} has moved", colorMoving);
            log.debug("Move sent to player {}", playerTurn());
            log.debug("Waiting for player {} to move", playerTurn());
            ((ServerConsoleView) chessView).printBoard();
        } else {
            log.warn("{} sent an invalid move", colorMoving);
        }
    }

    private void listenToPlayer() {
        while (true) {
            PlayerColor currentTurn = playerTurn();
            PlayerHandler player = players.get(currentTurn);
            Message message = player.receiveMove();
            int[] parsedArgs = message.getNumericArguments();
            remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
            PlayerHandler otherPlayer = players.get(currentTurn.opposite());
            otherPlayer.addMoveToStack(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
            otherPlayer.sendStack();
            if (isEndGame()) {
                askUsersToPlayAgain();
            }
        }
    }

    @Override
    protected ChessPiece askUserForPromotion(String header, String question, ChessPiece[] options) {
        System.out.println(header);
        System.out.println(question);
        Message message = players.get(playerTurn()).receivePromotion();
        int[] parsedArgs = message.getNumericArguments();
        for (ChessPiece piece : options) {
            if (piece.getPieceType() == PieceType.values()[parsedArgs[0]]
                    && piece.getX() == parsedArgs[1]
                    && piece.getY() == parsedArgs[2]) {
                players.get(playerTurn().opposite()).addPromotionToStack(piece);
                return piece;
            }
        }
        return null;
    }

    private void askUsersToPlayAgain() {
        AtomicReference<Message> whiteMessage = new AtomicReference<>();
        Thread whiteThread = new Thread(() -> {
            whiteMessage.set(players.get(PlayerColor.WHITE).receiveReplay());
        });
        whiteThread.start();
        AtomicReference<Message> blackMessage = new AtomicReference<>();
        Thread blackThread = new Thread(() -> {
            blackMessage.set(players.get(PlayerColor.BLACK).receiveReplay());
        });
        blackThread.start();
        try {
            whiteThread.join();
            blackThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String goodResponse = "Yes";
        String badResponse = "No";
        String whiteResponse = whiteMessage.get().getArguments()[0];
        String blackResponse = blackMessage.get().getArguments()[0];
        if (whiteResponse.equals(goodResponse) && blackResponse.equals(goodResponse)) {
            players.get(PlayerColor.WHITE).addReplayToStack(goodResponse);
            players.get(PlayerColor.BLACK).addReplayToStack(goodResponse);
            restartGame();
        } else {
            players.get(PlayerColor.WHITE).addReplayToStack(badResponse);
            players.get(PlayerColor.BLACK).addReplayToStack(badResponse);
            players.get(PlayerColor.WHITE).sendStack();
            players.get(PlayerColor.BLACK).sendStack();
            System.exit(0);
        }
        players.get(PlayerColor.WHITE).sendStack();
        players.get(PlayerColor.BLACK).sendStack();
    }

    // -- Overrides for compatibility with the fact that it is a network server game

    @Override
    protected UserChoice askUserToPlayAgain(String header, String question, UserChoice[] options) {
        // NOTE: In order to avoid the blocking of the server, we don't ask the user to play again.
        //       We send the move to the client and then ask the clients if they want to play again
        //       with askUsersToPlayAgain().
        return null;
    }
}
