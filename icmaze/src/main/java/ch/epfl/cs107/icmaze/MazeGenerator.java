package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division
 * algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    private MazeGenerator() {
    }

    /**
     * Print the maze
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
     */
    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }

    public static int[][] createMaze(int width, int height, int difficulty) {

        int[][] grid = new int[height][width];
        // Initialisation d’une grille vide : 0 = chemin, 1 = mur

        recursiveDivide(grid, 0, 0, width, height, difficulty);
        // Lancement de la division récursive sur toute la zone

        return grid;
    }

    private static void recursiveDivide(int[][] grid, int x, int y, int width, int height, int difficulty) {

        // Cas d’arrêt : la région est trop petite pour être subdivisée
        if (width <= difficulty || height <= difficulty)
            return;

        // Choix de l’orientation du mur
        boolean verticalWall;
        if (width > height)
            verticalWall = true; // Région plus large → mur vertical
        else if (height > width)
            verticalWall = false; // Région plus haute → mur horizontal
        else
            verticalWall = random.nextBoolean(); // Carré → orientation aléatoire

        if (verticalWall) {

            // Sélection d’une colonne impaire où placer le mur vertical
            int wallX = x + randomOdd(width - 2);

            // Construction du mur vertical
            for (int line = y; line < y + height; line++) {
                grid[line][wallX] = WALL;
            }

            // Création d’une ouverture à une ligne paire
            int passageY = y + randomEven(height - 1);
            grid[passageY][wallX] = 0;

            // Calcul des dimensions des deux nouvelles sous-régions
            int widthLeft = wallX - x;
            int widthRight = x + width - (wallX + 1);

            // Appel récursif sur chaque sous-région
            recursiveDivide(grid, x, y, widthLeft, height, difficulty); // Région gauche
            recursiveDivide(grid, wallX + 1, y, widthRight, height, difficulty); // Région droite

        } else {

            // Sélection d’une ligne impaire où placer le mur horizontal
            int wallY = y + randomOdd(height - 1);

            // Construction du mur horizontal
            for (int column = x; column < x + width; column++) {
                grid[wallY][column] = WALL;
            }

            // Création d’une ouverture à une colonne paire
            int passageX = x + randomEven(width - 2);
            grid[wallY][passageX] = 0;

            // Calcul des dimensions des deux sous-régions
            int heightUp = wallY - y;
            int heightDown = y + height - (wallY + 1);

            // Appels récursifs
            recursiveDivide(grid, x, y, width, heightUp, difficulty); // Région du haut
            recursiveDivide(grid, x, wallY + 1, width, heightDown, difficulty); // Région du bas
        }
    }

}
