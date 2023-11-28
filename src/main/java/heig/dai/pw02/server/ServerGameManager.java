package heig.dai.pw02.server;

import heig.dai.pw02.ccp.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.ChessView.UserChoice;
import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.ChessString;
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

            Message message = player.awaitMove().join();
            int[] parsedArgs = message.getNumericArguments();
            remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);

            PlayerHandler otherPlayer = players.get(currentTurn.opposite());
            otherPlayer.sendMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);

            if (isEndGame()) {
                askUsersToPlayAgain();
            }
        }
    }

    @Override
    protected ChessPiece askUserForPromotion(String header, String question, ChessPiece[] options) {
        Message message = players.get(playerTurn()).awaitPromotion().join();
        int[] parsedArgs = message.getNumericArguments();
        for (ChessPiece piece : options) {
            if (piece.getPieceType() == PieceType.values()[parsedArgs[0]]
                    && piece.getX() == parsedArgs[1]
                    && piece.getY() == parsedArgs[2]) {
                players.get(playerTurn().opposite()).sendPromotion(piece);
                return piece;
            }
        }
        return null;
    }

    private void askUsersToPlayAgain() {
        var whitePlayer = players.white();
        var blackPlayer = players.black();

        // Await the replay message from both players
        var whiteAnswer = whitePlayer.awaitReplay();
        var blackAnswer = blackPlayer.awaitReplay();
        String whiteResponse = whiteAnswer.join().getArguments()[0];
        String blackResponse = blackAnswer.join().getArguments()[0];

        if (whiteResponse.equals(ChessString.YES) && blackResponse.equals(ChessString.YES)) {
            whitePlayer.sendReplay(ChessString.YES);
            blackPlayer.sendReplay(ChessString.YES);
            restartGame();
        } else {
            whitePlayer.sendReplay(ChessString.NO);
            blackPlayer.sendReplay(ChessString.NO);
            System.exit(0);
        }
    }

    @Override
    protected UserChoice askUserToPlayAgain(String header, String question, UserChoice[] options) {
        // NOTE: In order to avoid the blocking of the server, we don't ask the user to play again.
        //       We send the move to the client and then ask the clients if they want to play again
        //       with askUsersToPlayAgain().
        return null;
    }
}