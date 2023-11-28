package heig.dai.pw02.client;

import heig.dai.pw02.ccp.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.ChessView.UserChoice;
import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.ChessString;
import heig.poo.chess.views.gui.GUIView;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientGameManager extends GameManager {

    private final ServerHandler server;
    private final PlayerColor myColor;
    private boolean boardIsBlocked = false;

    public ClientGameManager(ServerHandler server) {
        this.server = server;
        log.info("Waiting to get a color from the server");
        this.myColor = server.awaitColor().join();
    }

    /**
     * Function used to start the game. In the case of a remote game, we start the GUIView and listen to the server.
     */
    public void start() {
        start(new GUIView(this, "Client - " + myColor.toString()));
    }

    @Override
    public void start(ChessView view) {
        super.start(view);
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }

    /**
     * Function used to listen to the server and make the move sent by the server.
     */
    private void listenMove() {
        server.awaitMove().thenAccept(message -> {
            int[] parsedArgs = message.getNumericArguments();
            remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
        });
    }

    /**
     * Function used to make a move. In the case of a remote game, we send the move to the server.
     *
     * @param fromX the x coordinate of the piece to move
     * @param fromY the y coordinate of the piece to move
     * @param toX   the x coordinate of the destination
     * @param toY   the y coordinate of the destination
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        if (boardIsBlocked) {
            return false;
        }

        ChessPiece piece = board.getPiece(fromX, fromY);
        if (Objects.isNull(piece)) {
            return false;
        }

        PlayerColor colorMoving = board.getPiece(fromX, fromY).getPlayerColor();
        if (colorMoving != myColor || playerTurn() != colorMoving) {
            return false;
        }

        if (super.move(fromX, fromY, toX, toY)) {
            server.sendMove(fromX, fromY, toX, toY);
            if (isEndGame()) {
                postGameActions();
            } else {
                listenMove();
            }

            return true;
        }

        log.error("Invalid move");
        return false;
    }

    /**
     * Ask the user for a promotion. In the case of a remote game, we send the choice to the server.
     *
     * @param header   the header of the question
     * @param question the question
     * @param options  the options
     * @return a piece of the type chosen by the user
     */
    @Override
    protected ChessPiece askUserForPromotion(String header, String question, ChessPiece[] options) {
        ChessPiece movingPiece = board.getPiece(options[0].getX(), options[0].getY());
        if (Objects.isNull(movingPiece)) {
            return null;
        }

        if (movingPiece.getPlayerColor() == myColor) {
            ChessPiece choice = super.askUserForPromotion(header, question, options);
            server.sendPromotion(choice);
            return choice;
        }

        log.debug("Waiting for the other player to choose a promotion");
        Message message = server.awaitPromotion().join();
        int[] parsedArgs = message.getNumericArguments();
        PieceType pieceType = PieceType.values()[parsedArgs[0]];
        for (ChessPiece piece : options) {
            if (piece.getPieceType() == pieceType && piece.getX() == parsedArgs[1] && piece.getY() == parsedArgs[2]) {
                return piece;
            }
        }

        return null;
    }

    /**
     * Ask the user to play again. In the case of a remote game, we send the choice to the server. If the user wants
     * to play again, we restart the game with restartGame().
     *
     * @param header   the header of the question
     * @param question the question
     * @param choices  the choices
     * @return the choice of the user
     */
    @Override
    protected UserChoice askUserToPlayAgain(String header, String question, UserChoice[] choices) {
        UserChoice choice = super.askUserToPlayAgain(header, question, choices);
        server.sendReplay(choice.textValue());
        if (choice.textValue().equals(ChessString.NO)) {
            server.disconnect();
            System.exit(0);
        }

        return choice;
    }

    @Override
    protected void postGameActions(boolean checkMate, boolean pat, boolean impossibleOfCheckMate) {
        // NOTE: Handled asynchronously when receiving the request from the server
    }

    protected void postGameActions() {
        super.postGameActions();
        boardIsBlocked = true;
        chessView.displayMessage("Waiting for the other player to choose");
        Message otherPlayerReplay = server.awaitReplay().join();
        String replay = otherPlayerReplay.getArguments()[0];
        if (replay.equals(ChessString.NO)) {
            System.exit(0);
        }

        boardIsBlocked = false;
        chessView.displayMessage(ChessString.playerToMove(playerTurn()));
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }


    /**
     * Private function used primarily to make the move sent by the server. Used in the move() function to avoid
     * duplicate code.
     *
     * @param fromX the x coordinate of the piece to move
     * @param fromY the y coordinate of the piece to move
     * @param toX   the x coordinate of the destination
     * @param toY   the y coordinate of the destination
     */
    private void remoteMove(int fromX, int fromY, int toX, int toY) {
        super.move(fromX, fromY, toX, toY);
        if (isEndGame()) {
            postGameActions();
        }
    }
}