package heig.poo.chess.engine.util;

/**
 * Class containing all the math methods used in the chess engine.
 * @author Vicky Butty
 * @author Loïc Herman
 * @author Massimo Stefani
 * @version 1.0
 */
public class ChessMath {

    /**
     * Method used to get the greatest common divisor of two numbers using the Euclidean algorithm.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The greatest common divisor of the two numbers.
     */
    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
