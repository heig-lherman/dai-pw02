package heig.dai.pw02.client;

import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.engine.util.Assertions;

import java.util.Objects;

public class ClientGameManager extends GameManager {
    private ServerHandler server;
    private PlayerColor myColor;

    /**
     * Function used to start the game. In the case of a remote game, we receive the color from the server.
     * @param view the view
     * @param server the server
     */
    public void start(ChessView view, ServerHandler server) {
        Assertions.assertNotNull(server, "Player cannot be null");
        this.server = server;
        this.myColor = server.receiveColor();
        super.start(view);
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }

    /**
     * Function used to listen to the server and make the move sent by the server.
     */
    private void listenMove() {
        Message message = server.receiveMove();
        Integer[] parsedArgs = Message.parseArgumentsToInt(message);
        remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
    }

    /**
     * Function used to make a move. In the case of a remote game, we send the move to the server.
     * @param fromX the x coordinate of the piece to move
     * @param fromY the y coordinate of the piece to move
     * @param toX the x coordinate of the destination
     * @param toY the y coordinate of the destination
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        ChessPiece piece = super.board.getPiece(fromX, fromY);
        if (Objects.isNull(piece)) {
            return false;
        }
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (colorMoving != myColor || playerTurn() != colorMoving) {
            return false;
        }
        if (remoteMove(fromX, fromY, toX, toY)) {
            server.addMoveToStack(fromX, fromY, toX, toY);
            new Thread(this::listenMove).start();
            server.sendStack();
            return true;
        }else {
            System.out.println("Invalid move");
            return false;
        }
    }

    /**
     * Ask the user for a promotion. In the case of a remote game, we send the choice to the server.
     * @param header  the header of the question
     * @param question the question
     * @param options the options
     * @return a piece of the type chosen by the user
     */
    @Override
    protected ChessPiece askUserForPromotion(String header, String question, ChessPiece[] options) {
        ChessPiece movingPiece = super.board.getPiece(options[0].getX(), options[0].getY());
        if (Objects.isNull(movingPiece)) {
            return null;
        }
        if (movingPiece.getPlayerColor() == myColor) {
            ChessPiece choice = super.askUserForPromotion(header, question, options);
            server.addPromotionToStack(choice);
            return choice;
        }
        System.out.println(header);
        System.out.println(question);
        Message message = server.receivePromotion();
        String[] parsedArgs = message.arguments().split(" ");
        PieceType pieceType = PieceType.valueOf(parsedArgs[0]);
        int x = Integer.parseInt(parsedArgs[1]);
        int y = Integer.parseInt(parsedArgs[2]);
        for (ChessPiece piece : options) {
            if (piece.getPieceType() == pieceType && piece.getX() == x && piece.getY() == y) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Ask the user to play again. In the case of a remote game, we send the choice to the server. If the user wants
     * to play again, we restart the game with restartGame().
     * @param header   the header of the question
     * @param question the question
     * @param choices  the choices
     * @return the choice of the user
     */
    @Override
    protected ChessView.UserChoice askUserToPlayAgain(String header, String question, ChessView.UserChoice[] choices) {
        System.out.println(header);
        System.out.println(question);
        ChessView.UserChoice choice = super.askUserToPlayAgain(header, question, choices);
        server.addReplayToStack(choice.textValue());
        server.sendStack();
        return choice;
    }

    /**
     * In order to make postGameActions() after sending the replay choice, we empty the function and call a new one
     * later in a new thread.
     * @param checkMate             indicates if the adversary king is in checkmate
     * @param pat                   indicates if there is a pat
     * @param impossibleOfCheckMate indicates if there is an impossibility of checkmate
     */
    protected void postGameActions(boolean checkMate, boolean pat, boolean impossibleOfCheckMate){
        return;
    }

    /**
     * Function used to restart the game. In the case of a remote game, we listen to the server to make the move.
     */
    protected void restartGame(){
        super.restartGame();
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }

    /**
     * Private function used primarily to make the move sent by the server. Used in the move() function to avoid
     * duplicate code.
     * @param fromX the x coordinate of the piece to move
     * @param fromY the y coordinate of the piece to move
     * @param toX  the x coordinate of the destination
     * @param toY the y coordinate of the destination
     * @return true if the move is valid, false otherwise
     */
    private boolean remoteMove(int fromX, int fromY, int toX, int toY) {
        boolean result = super.move(fromX, fromY, toX, toY);
        if(isEndGame()){
            new Thread(this::postGameActions).start();
        }
        return result;
    }
}
