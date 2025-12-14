package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class AireLabyrinthique extends ICMazeArea {
    private AreaPortals portalEnter; // il permet d entrer dans le labyrinthe associé
    private AreaPortals portalExit;
    protected int keyId;
    public final static int keyIdL1 = Integer.MAX_VALUE;
    public final static int keyIdL2 = Integer.MAX_VALUE - 1;
    public final static int keyIdL3 = Integer.MAX_VALUE - 2;
    public final static int keyIdL4 = Integer.MAX_VALUE - 3;
    // private final String gridName;

    /** Matrice du labyrinthe : 0 = chemin, 1 = mur */
    protected int[][] mazeGrid;

    private int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setPortalEnter(AreaPortals portalEnter) {
        this.portalEnter = portalEnter;
    }

    public void setPortalExit(AreaPortals portalExit) {
        this.portalExit = portalExit;
    }

    public AireLabyrinthique(String behaviorName, int size, AreaPortals portalEnter, AreaPortals portalExit, int Keyid,
            int difficulty) {
        super(behaviorName, size);
        this.portalEnter = portalEnter;
        this.portalExit = portalExit;
        this.keyId = Keyid;
        this.difficulty = difficulty;
        // this.gridName = gridName;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    @Override
    protected void createArea() {
        mazeGrid = MazeGenerator.createMaze(size, size, difficulty);
        MazeGenerator.printMaze(mazeGrid, getEntryArrivalCoordinates(), getExitArrivalCoordinates());

        buildGraphFromMaze();

        placeRocks();

        Random rng = RandomGenerator.rng;

        placeRandomKey(rng);

        placeLogMonsters(rng);

    }

    private void buildGraphFromMaze() {
        graph.getNodes().clear();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                if (mazeGrid[y][x] == 0) {
                    DiscreteCoordinates c = new DiscreteCoordinates(x + 1, y + 1);

                    boolean left = (x > 0 && mazeGrid[y][x - 1] == 0);
                    boolean right = (x < size - 1 && mazeGrid[y][x + 1] == 0);
                    boolean down = (y > 0 && mazeGrid[y - 1][x] == 0);
                    boolean up = (y < size - 1 && mazeGrid[y + 1][x] == 0);

                    graph.addNode(c, left, up, right, down);
                }
            }
        }

    }

    protected void placeRandomKey(Random rng) {

        List<DiscreteCoordinates> candidates = new ArrayList<>(graph.keySet());

        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit = getExitArrivalCoordinates();
        candidates.remove(entry);
        candidates.remove(exit);

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates, rng);

        DiscreteCoordinates pos = candidates.get(0);

        System.out.println("Key for " + getTitle() + " at " + pos);

        Key key = new Key(this, Orientation.DOWN, pos, keyId);
        registerActor(key);
        // DiscreteCoordinates pos = getRandomFreeCell(rng);
        // Key key = new Key(this, Orientation.DOWN, pos, keyId);
        // registerActor(key);
    }

    /** Placement des rochers en fonction de mazeGrid */
    private void placeRocks() {

        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit = getExitArrivalCoordinates();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                if (mazeGrid[y][x] == 1) {

                    DiscreteCoordinates pos = new DiscreteCoordinates(x + 1, y + 1);

                    if (!pos.equals(entry) && !pos.equals(exit)) {
                        registerActor(new Rock(this, pos));
                    }
                }
            }
        }
    }

    /**
     * Retourne les coordonnées d’arrivée dans l’aire pour le portail d’entrée.
     */
    protected DiscreteCoordinates getEntryArrivalCoordinates() {
        return getArrivalCoordinatesForPortal(portalEnter);
    }

    /**
     * Retourne les coordonnées d’arrivée dans l’aire pour le portail de sortie.
     */
    protected DiscreteCoordinates getExitArrivalCoordinates() {
        return getArrivalCoordinatesForPortal(portalExit);
    }

    /**
     * Retourne les coordonnées d’arrivée dans l’aire selon le portail donné.
     */
    private DiscreteCoordinates getArrivalCoordinatesForPortal(AreaPortals portal) {

        return switch (portal) {
            case N -> new DiscreteCoordinates(size / 2, size);
            case S -> new DiscreteCoordinates(size / 2, 1);
            case W -> new DiscreteCoordinates(size, size / 2);
            case E -> new DiscreteCoordinates(1, size / 2);
        };
    }

    protected void placeLogMonsters(Random rng) {

        double diffRatio = Math.min(1.0, (double) Difficulty.HARDEST / (double) difficulty);

        int maxEnnemies = 3;
        int ennemyCount = 0;

        double pEnnemy = 0.25 + 0.60 * diffRatio;

        // c'est ça qui permet d'ajouter des ennemis sinon il y en a toujours 0oui

        for (int i = 0; i < maxEnnemies; i++) {
            if (rng.nextDouble() < pEnnemy) {
                ennemyCount++;
            }
        }

        if (ennemyCount == 0) {
            return; // aucun
        }

        List<DiscreteCoordinates> candidates = new ArrayList<>(graph.keySet());

        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit = getExitArrivalCoordinates();
        candidates.remove(entry);
        candidates.remove(exit);

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates, rng);

        for (int i = 0; i < ennemyCount && i < candidates.size(); i++) {
            DiscreteCoordinates pos = candidates.get(i);

            LogMonster.State initialState = chooseInitialState(rng, diffRatio);

            LogMonster monster = new LogMonster(this, Orientation.DOWN, pos, initialState);

            registerActor(monster);
        }
    }

    private LogMonster.State chooseInitialState(Random rng, double diffRatio) {
        double pTarget = 0.10 + 0.70 * diffRatio;
        double pRandom = 0.20;
        double pSleeping = 1.0 - pTarget - pRandom;

        double r = rng.nextDouble(); // valeur aléatoire

        if (r < pSleeping) {
            return LogMonster.State.SLEEPING;
        } else if (r < pSleeping + pRandom) {
            return LogMonster.State.RANDOM;
        } else {
            return LogMonster.State.TARGETING;
        }
    }



}
