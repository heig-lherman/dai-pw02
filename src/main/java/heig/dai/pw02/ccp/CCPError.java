package heig.dai.pw02.ccp;

import lombok.Getter;

@Getter
public enum CCPError {
    INVALID_MESSAGE("Invalid message"),
    INVALID_NBR_ARGUMENTS("Invalid number of arguments"),
    INVALID_MOVE("Invalid move"),
    INVALID_PROMOTION("Invalid promotion"),
    INVALID_REPLAY("Invalid replay"),
    INVALID_COLOR("Invalid color"),
    INVALID_PIECE("Invalid piece");

    private final String description;

    CCPError(String description) {
        this.description = description;
    }

    public String toString() {
        return "Error: " + this.name() + " - " + this.description;
    }
}
