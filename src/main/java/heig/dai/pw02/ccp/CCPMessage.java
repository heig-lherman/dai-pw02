package heig.dai.pw02.ccp;

import java.util.List;

public enum CCPMessage {
    HELLO, COLOR, OK, MOVE, YOURTURN, STALEMATE, CHECKMATE, NOMATERIAL, ERROR;

    List<CCPEntity> entities;

    CCPMessage(CCPEntity... entities) {
        this.entities = List.of(entities);
    }

}
