package heig.dai.pw02.ccp;

import java.util.List;

public enum CCPMessage {
    COLOR, MOVE, PROMOTION, ERROR;

    List<CCPEntity> entities;

    CCPMessage(CCPEntity... entities) {
        this.entities = List.of(entities);
    }

}
