package ch.epfl.cs107.icmaze;

/**
 * Difficulty
 * Represents the difficulty levels for maze generation.
 * The values correspond to the minimum room size in the recursive division
 * algorithm.
 */
public final class Difficulty {

    /**
     * Private constructor to prevent instantiation
     */
    private Difficulty() {
    }

    // Number represents "tightness" of the maximum room subdivision. A lower number
    // effectively makes the room harder
    public static final int EASIEST = 10;
    public static final int EASY = 8;
    public static final int MEDIUM = 6;
    public static final int HARD = 4;
    public static final int HARDEST = 2;
}
