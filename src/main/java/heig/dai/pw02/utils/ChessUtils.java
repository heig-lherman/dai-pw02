package heig.dai.pw02.utils;public class ChessUtils {
    public static int intCoordinateToIndex(char c) {
        assert (c >= '1' && c <= '9');
        return c - '1';
    }
    public static String indexToChar(int index) {
        return switch (index) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            case 4 -> "e";
            case 5 -> "f";
            case 6 -> "g";
            case 7 -> "h";
            default -> throw new RuntimeException("Invalid index");
        };
    }
}
