package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;




/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {

    private static final Random random = RandomGenerator.rng;
    private static final Random RNG = RandomGenerator.rng;
    private static final int PATH = 0;
    private static final int WALL = 1;

    private MazeGenerator(){}


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
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == WALL ? "███" : "   ");
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

    /**
     * Retourne un nombre impair dans l’intervalle [min, max]
     * en utilisant randomOdd(max).
     */
    private static int computeOddInRange(int min, int max) {

        // Inversion si min > max
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }

        int value;

        do {
            value = randomOdd(max);   // tirage impair entre 1 et max
        } while (value < min);        // on s'assure qu'il est ≥ min

        return value;
    }

    /**
     * Retourne un nombre pair dans l’intervalle [min, max]
     * en utilisant randomEven(max).
     */
    private static int computeEvenInRange(int min, int max) {

        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }

        int value;

        do {
            value = randomEven(max);  // tirage pair entre 0 et max
        } while (value < min);        // on s'assure qu'il est ≥ min

        return value;
    }



    /**
     * Génère un labyrinthe en utilisant l’algorithme de division récursive.
     *
     * @param width      largeur totale de la grille
     * @param height     hauteur totale de la grille
     * @param difficulty taille minimale d’une sous-région avant d’arrêter la division
     * @return une matrice height × width contenant 0 (chemin) et 1 (mur)
     */
    public static int[][] createMaze(int width, int height, int difficulty) {

        // On crée une matrice vide de taille height × width
        int[][] maze = new int[height][width];

        // On initialise toutes les cases à PATH (0), donc aucun mur au début
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = PATH;
            }
        }

        // On ajoute une bordure de murs autour de toute la grille
        // Cela garantit que la récursion reste dans les limites
        addBorderWalls(maze);

        // On lance l’algorithme de division récursive
        // On ne subdivise que l’intérieur (on ignore la bordure)
        splitRegionRecursively(
                maze,
                1,               // x de la sous-région (coin haut-gauche)
                1,               // y de la sous-région (coin haut-gauche)
                width - 2,       // largeur utile, sans la bordure
                height - 2,      // hauteur utile, sans la bordure
                difficulty       // profondeur minimale pour continuer à diviser
        );

        // On retourne le labyrinthe généré
        return maze;
    }




    /**
     * Ajoute une bordure de murs tout autour de la grille.
     *
     * Objectif :
     *   - créer un cadre solide autour du labyrinthe
     *   - empêcher la récursion de sortir de la zone
     *
     * @param maze la matrice représentant le labyrinthe
     */
    private static void addBorderWalls(int[][] maze) {

        // On récupère les dimensions de la grille
        int height = maze.length;
        int width = maze[0].length;

        // --- Bordure du haut et du bas ---
        // On parcourt toute la ligne du haut (y = 0)
        // et toute la ligne du bas (y = height - 1)
        for (int x = 0; x < width; x++) {
            maze[0][x] = WALL;                // mur sur la ligne du haut
            maze[height - 1][x] = WALL;       // mur sur la ligne du bas
        }

        // --- Bordure gauche et droite ---
        // On parcourt chaque ligne pour mettre un mur en x = 0 et x = width - 1
        for (int y = 0; y < height; y++) {
            maze[y][0] = WALL;                // mur côté gauche
            maze[y][width - 1] = WALL;        // mur côté droit
        }
    }



    /**
     * Divise récursivement une sous-région du labyrinthe en deux parties,
     * en traçant un mur (horizontal ou vertical) avec une ouverture.
     *
     * Principes de l’algorithme :
     *   1. Si la région est trop petite → STOP (cas de base)
     *   2. On choisit aléatoirement entre mur horizontal ou vertical
     *      (en privilégiant la direction dominante)
     *   3. On trace un mur complet dans la sous-région
     *   4. On crée une ouverture dans ce mur
     *   5. On applique récursivement sur les deux nouvelles sous-régions
     *
     * @param maze      grille contenant le labyrinthe
     * @param x         colonne du coin haut-gauche de la sous-région
     * @param y         ligne du coin haut-gauche de la sous-région
     * @param width     largeur de la sous-région
     * @param height    hauteur de la sous-région
     * @param minSize   taille minimale pour continuer la récursion
     */
    private static void splitRegionRecursively(
            int[][] maze,
            int x, int y,
            int width, int height,
            int minSize
    ) {

        // 1) Cas d'arrêt :
        //    Si la région est trop petite, on arrête de diviser.
        if (width <= minSize || height <= minSize) {
            return; // fin de la récursion ici
        }

        // 2) Choix de l’orientation du mur :
        //    - si la région est plus haute que large → mur horizontal
        //    - si la région est plus large → mur vertical
        //    - si pareil → tirage aléatoire
        boolean horizontal =
                width < height || (width == height && RNG.nextBoolean());

        // ======================================================================
        // ========================= MUR HORIZONTAL ==============================
        // ======================================================================
        if (horizontal) {

            // On choisit une ligne impaire dans laquelle tracer le mur
            int wallY = computeOddInRange(y + 1, y + height - 2);



            // On trace un mur horizontal complet
            for (int xx = x; xx < x + width; xx++) {
                maze[wallY][xx] = WALL;
            }

            // On crée une seule ouverture dans ce mur
            int passageX = computeEvenInRange(x, x + width - 1);

            maze[wallY][passageX] = PATH;

            // Définition des deux sous-régions :
            // Région du haut
            int topHeight = wallY - y;

            // Région du bas
            int bottomY = wallY + 1;
            int bottomHeight = y + height - bottomY;

            // Appels récursifs sur les deux sous-régions
            splitRegionRecursively(maze, x, y, width, topHeight, minSize);
            splitRegionRecursively(maze, x, bottomY, width, bottomHeight, minSize);

        }

        // ======================================================================
        // ========================== MUR VERTICAL ===============================
        // ======================================================================
        else {

            // On choisit une colonne impaire dans laquelle tracer le mur
            int wallX = computeOddInRange(x + 1, x + width - 2);


            // On trace un mur vertical complet
            for (int yy = y; yy < y + height; yy++) {
                maze[yy][wallX] = WALL;
            }

            // On crée une ouverture dans ce mur
            int passageY = computeEvenInRange(y, y + height - 1);

            maze[passageY][wallX] = PATH;

            // Définition des sous-régions :
            int leftWidth = wallX - x;           // région gauche
            int rightX = wallX + 1;              // début région droite
            int rightWidth = x + width - rightX; // largeur région droite

            // Appels récursifs sur les deux sous-régions
            splitRegionRecursively(maze, x, y, leftWidth, height, minSize);
            splitRegionRecursively(maze, rightX, y, rightWidth, height, minSize);
        }
    }




}

