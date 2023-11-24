package heig.dai.pw02.server;

import heig.dai.pw02.ccp.CCPMessage;
import heig.dai.pw02.model.Message;
import heig.poo.chess.PlayerColor;

public record PlayerPair(
        PlayerHandler white,
        PlayerHandler black
) {

    public PlayerHandler get(PlayerColor color) {
        return switch (color) {
            case WHITE -> white;
            case BLACK -> black;
        };
    }

    public void sendColors() {
        white.sendColor(PlayerColor.WHITE);
        black.sendColor(PlayerColor.BLACK);
    }
}
