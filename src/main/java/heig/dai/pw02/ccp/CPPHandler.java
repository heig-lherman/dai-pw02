package heig.dai.pw02.ccp;

import heig.dai.pw02.model.Message;

public interface CPPHandler {

    void sendMove(int fromX, int fromY, int toX, int toY);

    Message receiveMove();

    void sendMessage(Message message);

    Message receiveMessage();

}
