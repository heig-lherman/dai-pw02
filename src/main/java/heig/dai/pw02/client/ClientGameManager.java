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
    public void start(ChessView view, ServerHandler server) {
        Assertions.assertNotNull(server, "Player cannot be null");
        this.server = server;
        this.myColor = server.receiveColor();
        super.start(view);
        if (myColor == PlayerColor.BLACK) {
            listenMove();
        }
    }

    private void listenMove() {
        Message message = server.receiveMove();
        Integer[] parsedArgs = Message.parseArgumentsToInt(message);
        remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
    }

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

    @Override
    protected ChessView.UserChoice askUserToPlayAgain(String header, String question, ChessView.UserChoice[] choices) {
        System.out.println(header);
        System.out.println(question);
        ChessView.UserChoice choice = super.askUserToPlayAgain(header, question, choices);
        server.addReplayToStack(choice.textValue());
        return choice;
    }

    protected void postGameActions(boolean checkMate, boolean pat, boolean impossibleOfCheckMate){
        return;
    }

    private boolean remoteMove(int fromX, int fromY, int toX, int toY) {
        boolean result = super.move(fromX, fromY, toX, toY);
        if(isEndGame()){
            new Thread(this::postGameActions).start();
        }
        return result;
    }
}
