package heig.dai.pw02;

import heig.dai.pw02.ccp.CCPError;
import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.ccp.Message;
import heig.dai.pw02.client.ServerHandler;
import heig.dai.pw02.server.PlayerHandler;
import heig.dai.pw02.server.PlayerPair;
import heig.poo.chess.PlayerColor;
import heig.poo.chess.engine.piece.Pawn;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CCPTests {
    private final static int PORT = 1234;
    private static final ServerHandler[] servers = new ServerHandler[2];
    private static final List<PlayerHandler> players = new ArrayList<>();
    private static final Semaphore wait_server = new Semaphore(0);
    private static final Semaphore wait_clients = new Semaphore(0);
    private static final Semaphore wait_c0 = new Semaphore(0);


    @Test
    @Order(1)
    public void launchAllTests() throws InterruptedException {
        new Thread(CCPTests::launchServer).start();
        wait_server.acquire();
        new Thread(CCPTests::launchClient0).start();
        new Thread(CCPTests::launchClient1).start();
        wait_clients.acquire();
        wait_clients.acquire();
    }
    private static void launchServer() {
        Socket clientSocket;
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    wait_server.release();
                    System.out.println("Waiting for connection");
                    clientSocket = serverSocket.accept();
                    players.add(new PlayerHandler(clientSocket));
                    if(players.size() == 2){
                        new Thread(() -> {
                            try {
                                var pair = new PlayerPair(
                                        players.get(0),
                                        players.get(1)
                                );
                                pair.sendColors();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error while accepting connection");
                }
            }
        } catch (IOException e) {
            System.out.println("Error while creating server socket");
        }
    }
    private static void launchClient0(){
        Socket socket;
        try{
            socket = new Socket("localhost", PORT);
            servers[0] = new ServerHandler(socket);
            wait_c0.release();
            servers[0].awaitColor().join();
            wait_clients.release();
        } catch (Exception e){
            System.out.println("Error while connecting to the server");
            e.printStackTrace(); // Imprime la pile d'appels complète
            System.exit(0);
        }
    }

    private static void launchClient1(){
        Socket socket;
        try{
            wait_c0.acquire();
            socket = new Socket("localhost", PORT);
            servers[1] = new ServerHandler(socket);
            servers[1].awaitColor().join();
            wait_clients.release();
        } catch (Exception e){
            System.out.println("Error while connecting to the server");
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Test
    @Order(2)
    public void simpleMoveClientToServer(){
        int[] moves = {0, 0, 1, 1};
        servers[0].sendMove(moves[0], moves[1], moves[2], moves[3]);
        Message mString = players.get(0).awaitMove().join();
        int[] parsedArgs = mString.getNumericArguments();
        assertEquals(mString.getType(), CCPMessage.MOVE);
        assertArrayEquals(parsedArgs, moves);
    }

    @Test
    @Order(3)
    public void simpleMoveServerToClient(){
        int[] moves = {0, 0, 1, 3};
        players.get(0).sendMove(moves[0], moves[1], moves[2], moves[3]);
        Message mString = servers[0].awaitMove().join();
        int[] parsedArgs = mString.getNumericArguments();
        assertEquals(mString.getType(), CCPMessage.MOVE);
        assertArrayEquals(parsedArgs, moves);
    }

    @Test
    @Order(4)
    public void errorInvalidMessage(){
        int[] moves = {0, 0, 1, 3};
        servers[0].sendMove(moves[0], moves[1], moves[2], moves[3]);
        Message m = players.get(0).awaitPromotion().join();
        assertEquals(m.getType(), CCPMessage.ERROR);
        CCPError error = CCPError.values()[m.getNumericArguments()[0]];
        assertEquals(error, CCPError.INVALID_MESSAGE);
    }

    @Test
    @Order(5)
    public void errorInvalidMoveSamePoint(){
        int[] moves = {0, 0, 0, 0};
        servers[0].sendMove(moves[0], moves[1], moves[2], moves[3]);
        Message mString = players.get(0).awaitMove().join();
        assertEquals(mString.getType(), CCPMessage.ERROR);
        CCPError error = CCPError.values()[mString.getNumericArguments()[0]];
        assertEquals(error, CCPError.INVALID_MOVE);
    }

    @Test
    @Order(6)
    public void errorInvalidMoveOutOfBoard(){
        int[] moves = {100, 100, 100, 100};
        servers[0].sendMove(moves[0], moves[1], moves[2], moves[3]);
        Message mString = players.get(0).awaitMove().join();
        assertEquals(mString.getType(), CCPMessage.ERROR);
        CCPError error = CCPError.values()[mString.getNumericArguments()[0]];
        assertEquals(error, CCPError.INVALID_MOVE);
    }

    @Test
    @Order(7)
    public void errorInvalidReplay(){
        String replay = "Maybe";
        servers[0].sendReplay(replay);
        Message mString = players.get(0).awaitReplay().join();
        assertEquals(mString.getType(), CCPMessage.ERROR);
        CCPError error = CCPError.values()[mString.getNumericArguments()[0]];
        assertEquals(error, CCPError.INVALID_REPLAY);
    }

    @Test
    @Order(8)
    public void errorInvalidPromotion(){
        servers[0].sendPromotion(new Pawn(PlayerColor.WHITE, 0, 0));
        Message mString = players.get(0).awaitPromotion().join();
        assertEquals(mString.getType(), CCPMessage.ERROR);
        CCPError error = CCPError.values()[mString.getNumericArguments()[0]];
        assertEquals(error, CCPError.INVALID_PROMOTION);
    }

}
