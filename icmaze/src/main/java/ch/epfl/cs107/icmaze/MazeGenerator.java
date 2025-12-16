package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * MazeGenerator
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    /**
     * Private constructor to prevent instantiation
     */
    private MazeGenerator() {
    }

    /**
     * Print the maze
     * @param grid (int[][]): The maze grid
     * @param start (DiscreteCoordinates): Start position
     * @param end (DiscreteCoordinates): End position
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Print top border
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        // Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y)
                    System.out.print(" S ");
                else if (x == end.x && y == end.y)
                    System.out.print(" E ");
                else
                    System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (assuming max > 0).
     * @param max (int): The maximum value
     * @return (int): A random odd number
     */
    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     * @param max (int): The maximum value
     * @return (int): A random even number
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Create a maze grid
     * @param width (int): Width of the maze
     * @param height (int): Height of the maze
     * @param difficulty (int): Minimum size of a room
     * @return (int[][]): The generated maze grid
     */
    public static int[][] createMaze(int width, int height, int difficulty) {

        int[][] grid = new int[height][width];
        // Initialize an empty grid: 0 = path, 1 = wall

        recursiveDivide(grid, 0, 0, width, height, difficulty);
        // Start recursive division on the whole area

        return grid;
    }

    /**
     * Recursive division algorithm to generate the maze
     * @param grid (int[][]): The maze grid
     * @param x (int): Top-left x coordinate of the region
     * @param y (int): Top-left y coordinate of the region
     * @param width (int): Width of the region
     * @param height (int): Height of the region
     * @param difficulty (int): Minimum size of a room
     */
    private static void recursiveDivide(int[][] grid, int x, int y, int width, int height, int difficulty) {

        // Base case: region is too small to be subdivided
        if (width <= difficulty || height <= difficulty)
            return;

        // Choose wall orientation
        boolean verticalWall;
        if (width > height)
            verticalWall = true; // Wider region -> vertical wall
        else if (height > width)
            verticalWall = false; // Taller region -> horizontal wall
        else
            verticalWall = random.nextBoolean(); // Square -> random orientation

        if (verticalWall) {

            // Select an odd column to place the vertical wall
            int wallX = x + randomOdd(width - 2);

            // Build the vertical wall
            for (int line = y; line < y + height; line++) {
                grid[line][wallX] = WALL;
            }

            // Create an opening at an even line
            int passageY = y + randomEven(height - 1);
            grid[passageY][wallX] = 0;

            // Calculate dimensions of the two new sub-regions
            int widthLeft = wallX - x;
            int widthRight = x + width - (wallX + 1);

            // Recursive call on each sub-region
            recursiveDivide(grid, x, y, widthLeft, height, difficulty); // Left region
            recursiveDivide(grid, wallX + 1, y, widthRight, height, difficulty); // Right region

        } else {

            // Select an odd line to place the horizontal wall
            int wallY = y + randomOdd(height - 1);

            // Build the horizontal wall
            for (int column = x; column < x + width; column++) {
                grid[wallY][column] = WALL;
            }

            // Create an opening at an even column
            int passageX = x + randomEven(width - 2);
            grid[wallY][passageX] = 0;

            // Calculate dimensions of the two new sub-regions
            int heightUp = wallY - y;
            int heightDown = y + height - (wallY + 1);

            // Recursive calls
            recursiveDivide(grid, x, y, width, heightUp, difficulty); // Upper region
            recursiveDivide(grid, x, wallY + 1, width, heightDown, difficulty); // Lower region
        }
    }
}
