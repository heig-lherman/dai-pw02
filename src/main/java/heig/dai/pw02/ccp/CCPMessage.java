package heig.dai.pw02.ccp;

public enum CCPMessage {
    COLOR(1),
    MOVE(4),
    PROMOTION(3),
    REPLAY(1),
    ERROR(1);

    private final int nbrArguments;

    CCPMessage(int nbrArguments) {
        this.nbrArguments = nbrArguments;
    }

    public int nbrArguments() {
        return nbrArguments;
    }

}
