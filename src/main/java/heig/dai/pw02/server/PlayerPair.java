package heig.dai.pw02.server;

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
}
