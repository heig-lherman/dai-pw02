package heig.dai.pw02.server;

import heig.dai.pw02.model.Message;
import heig.poo.chess.ChessView;
import heig.poo.chess.PieceType;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.GameManager;
import heig.poo.chess.engine.piece.ChessPiece;
import heig.poo.chess.views.gui.GUIView;

import java.util.concurrent.atomic.AtomicReference;

public final class ServerGameManager extends GameManager {

    private final PlayerPair players;

    public ServerGameManager(PlayerPair players) {
        super();
        this.players = players;
        this.players.sendColors();
    }

    public void start() {
        super.start(new GUIView(this, "Server"));
        listenToPlayer();
    }

    public void remoteMove(int fromX, int fromY, int toX, int toY) {
        PlayerColor colorMoving = super.board.getPiece(fromX, fromY).getPlayerColor();
        if (super.move(fromX, fromY, toX, toY)) {
            System.out.println(colorMoving + " has moved");
            System.out.println("Move sent to player " + playerTurn());
            System.out.println("Waiting for player " + playerTurn() + " to move");
        }else {
            System.out.println("Invalid move");
        }
    }

    private void listenToPlayer() {
        while (true) {
            PlayerColor currentTurn = playerTurn();
            PlayerHandler player = players.get(currentTurn);
            Message message = player.receiveMove();
            Integer[] parsedArgs = Message.parseArgumentsToInt(message);
            remoteMove(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
            PlayerHandler otherPlayer = players.get(currentTurn.opposite());
            otherPlayer.addMoveToStack(parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
            otherPlayer.sendStack();
            if(isEndGame()){
                askUsersToPlayAgain();
            }
        }
    }

    @Override
    public boolean move(int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    protected ChessPiece askUserForPromotion(String header, String question, ChessPiece[] options) {
        System.out.println(header);
        System.out.println(question);
        Message message = players.get(playerTurn()).receivePromotion();
        String[] parsedArgs = message.arguments().split(" ");
        PieceType pieceType = PieceType.valueOf(parsedArgs[0]);
        int x = Integer.parseInt(parsedArgs[1]);
        int y = Integer.parseInt(parsedArgs[2]);
        for (ChessPiece piece : options) {
            if (piece.getPieceType() == pieceType && piece.getX() == x && piece.getY() == y) {
                players.get(playerTurn().opposite()).addPromotionToStack(piece);
                return piece;
            }
        }
        return null;
    }

    /**
     * In order to avoid the blocking of the server, we don't ask the user to play again. We send the move to the
     * client and then ask the clients if they want to play again with askUsersToPlayAgain().
     * @return null
     */
    @Override
    protected ChessView.UserChoice askUserToPlayAgain(String header, String question, ChessView.UserChoice[] options) {
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

        String whiteResponse = Message.parseArgumentsToString(whiteMessage.get())[0];
        String blackResponse = Message.parseArgumentsToString(blackMessage.get())[0];
        if (whiteResponse.equals("Yes") && blackResponse.equals("Yes")) {
            restartGame();
        }
    }
}
