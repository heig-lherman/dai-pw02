package heig.dai.pw02.ccp;

import java.util.List;

public enum CCPMessage {
    HELLO(CCPEntity.SERVER, CCPEntity.CLIENT),
    OK(CCPEntity.CLIENT, CCPEntity.SERVER),
    MOVE(CCPEntity.CLIENT, CCPEntity.SERVER),
    YOURTURN(CCPEntity.SERVER),
    STALEMATE(CCPEntity.SERVER),
    CHECKMATE(CCPEntity.SERVER),
    NOMATERIAL(CCPEntity.SERVER),
    ERROR(CCPEntity.SERVER);

    List<CCPEntity> entities;

    CCPMessage(CCPEntity... entities) {
        this.entities = List.of(entities);
    }

}
