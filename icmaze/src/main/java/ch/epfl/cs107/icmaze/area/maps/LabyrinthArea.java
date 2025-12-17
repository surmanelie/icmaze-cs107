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

public abstract class LabyrinthArea extends ICMazeArea {
    private AreaPortals portalEnter; // il permet d entrer dans le labyrinthe associé
    private AreaPortals portalExit;
    protected int keyId;
    public final static int keyIdL1 = Integer.MAX_VALUE;
//    public final static int keyIdL2 = Integer.MAX_VALUE - 1;
//    public final static int keyIdL3 = Integer.MAX_VALUE - 2;
//    public final static int keyIdL4 = Integer.MAX_VALUE - 3;
//    // private final String gridName;

    /** Matrice du labyrinthe : 0 = chemin, 1 = mur */
    protected int[][] mazeGrid;

    private int difficulty;

//    public int getDifficulty() {
//        return difficulty;
//    }
//
//    public void setDifficulty(int difficulty) {
//        this.difficulty = difficulty;
//    }

    public void setPortalEnter(AreaPortals portalEnter) {
        this.portalEnter = portalEnter;
    }

    public void setPortalExit(AreaPortals portalExit) {
        this.portalExit = portalExit;
    }

    public LabyrinthArea(String behaviorName, int size, AreaPortals portalEnter, AreaPortals portalExit, int Keyid,
            int difficulty) {
        super(behaviorName, size);
        this.portalEnter = portalEnter;
        this.portalExit = portalExit;
        this.keyId = Keyid;
        this.difficulty = difficulty;
    }

    public int getKeyId() {
        return keyId;
    }

//    public void setKeyId(int keyId) {
//        this.keyId = keyId;
//    }

    private boolean isvalid(DiscreteCoordinates c) {
        return c.x >= 1 && c.x <= size && c.y >= 1 && c.y <= size;
    }

    @Override
    protected void createArea() {
        // 1. On génère le labyrinthe brut (avec des murs potentiellement devant les
        // portes)
        mazeGrid = MazeGenerator.createMaze(size, size, difficulty);

        // 2. On récupère les positions des portes
        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit = getExitArrivalCoordinates();

        // 3. --- CORRECTION CRITIQUE ---
        // On calcule les cases juste DEVANT les portes (à l'intérieur du jeu)
        DiscreteCoordinates entryInside = getInsideCell(portalEnter);
        DiscreteCoordinates exitInside = getInsideCell(portalExit);

        // 4. On force le nettoyage (0 = chemin) :
        // - Sur la case du portail elle-même
        // - ET sur la case juste devant
        if (isvalid(entry))
            mazeGrid[entry.y - 1][entry.x - 1] = 0;
        if (isvalid(entryInside))
            mazeGrid[entryInside.y - 1][entryInside.x - 1] = 0;

        if (isvalid(exit))
            mazeGrid[exit.y - 1][exit.x - 1] = 0;
        if (isvalid(exitInside))
            mazeGrid[exitInside.y - 1][exitInside.x - 1] = 0;
        // -----------------------------

        // 5. Le reste ne change pas (construction du graphe, placement des objets...)
        MazeGenerator.printMaze(mazeGrid, getEntryArrivalCoordinates(), getExitArrivalCoordinates());

        buildGraphFromMaze();

        placeRocks(); // Maintenant, placeRocks ne mettra plus de rocher devant la porte car c'est
                      // devenu un 0 !

        Random rng = RandomGenerator.rng;

        placeRandomKey(rng);

        placeLogMonsters(rng);
        placeSpeedBalls(rng);
    }

    private void placeSpeedBalls(Random rng) {
        List<DiscreteCoordinates> candidates = new ArrayList<>(graph.keySet());
        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit = getExitArrivalCoordinates();

        candidates.remove(entry);
        candidates.remove(exit);

        // Ensure we have at least 2 spots
        if (candidates.size() < 2) {
            return;
        }

        Collections.shuffle(candidates, rng);

        DiscreteCoordinates posBlue = candidates.get(0);
        DiscreteCoordinates posRed = candidates.get(1);

        registerActor(new ch.epfl.cs107.icmaze.actor.collectable.BlueSpeedBall(this, Orientation.DOWN, posBlue));
        registerActor(new ch.epfl.cs107.icmaze.actor.collectable.RedSpeedBall(this, Orientation.DOWN, posRed));
    }

    private void buildGraphFromMaze() {
        graph.getNodes().clear();

        List<DiscreteCoordinates> portalCells = getPortalCells();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                if (mazeGrid[y][x] == 0) {
                    DiscreteCoordinates c = new DiscreteCoordinates(x + 1, y + 1);

                    // Skip adding the node if it is a portal cell
                    if (portalCells.contains(c)) {
                        continue;
                    }

                    boolean left = (x > 0 && mazeGrid[y][x - 1] == 0
                            && !portalCells.contains(new DiscreteCoordinates(x, y + 1)));
                    boolean right = (x < size - 1 && mazeGrid[y][x + 1] == 0
                            && !portalCells.contains(new DiscreteCoordinates(x + 2, y + 1)));
                    boolean down = (y > 0 && mazeGrid[y - 1][x] == 0
                            && !portalCells.contains(new DiscreteCoordinates(x + 1, y)));
                    boolean up = (y < size - 1 && mazeGrid[y + 1][x] == 0
                            && !portalCells.contains(new DiscreteCoordinates(x + 1, y + 2)));

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

        Key key = new Key(this, Orientation.DOWN, pos, keyId);
        registerActor(key);
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
                        registerActor(new Rock(this, Orientation.DOWN, pos, this));
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
            case W -> new DiscreteCoordinates(1, size / 2);
            case E -> new DiscreteCoordinates(size, size / 2);
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

            LogMonster monster = new LogMonster(this, Orientation.DOWN, pos, initialState, this, difficulty);

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

    @Override
    public void onRockDestroyed(DiscreteCoordinates cell) {
        // Convertir coordonnée "aire" -> indices mazeGrid
        int gx = cell.x - 1;
        int gy = cell.y - 1;

        if (gx < 0 || gx >= size || gy < 0 || gy >= size)
            return;

        // 1) Ouvrir la cellule
        mazeGrid[gy][gx] = 0;

        // 2) Recréer le noeud ET rafraîchir les voisins pour mettre à jour les arêtes
        rebuildGraphNodeAt(gx, gy);
        if (gx > 0)
            rebuildGraphNodeAt(gx - 1, gy);
        if (gx < size - 1)
            rebuildGraphNodeAt(gx + 1, gy);
        if (gy > 0)
            rebuildGraphNodeAt(gx, gy - 1);
        if (gy < size - 1)
            rebuildGraphNodeAt(gx, gy + 1);
    }

    private void rebuildGraphNodeAt(int gx, int gy) {
        if (mazeGrid[gy][gx] != 0)
            return; // on ne met des noeuds que sur les chemins

        DiscreteCoordinates c = new DiscreteCoordinates(gx + 1, gy + 1);

        java.util.List<DiscreteCoordinates> portalCells = getPortalCells();

        // Skip adding the node if it is a portal cell
        if (portalCells.contains(c)) {
            return;
        }

        boolean left = (gx > 0 && mazeGrid[gy][gx - 1] == 0
                && !portalCells.contains(new DiscreteCoordinates(gx, gy + 1)));
        boolean right = (gx < size - 1 && mazeGrid[gy][gx + 1] == 0
                && !portalCells.contains(new DiscreteCoordinates(gx + 2, gy + 1)));
        boolean down = (gy > 0 && mazeGrid[gy - 1][gx] == 0
                && !portalCells.contains(new DiscreteCoordinates(gx + 1, gy)));
        boolean up = (gy < size - 1 && mazeGrid[gy + 1][gx] == 0
                && !portalCells.contains(new DiscreteCoordinates(gx + 1, gy + 2)));

        graph.addNode(c, left, up, right, down);
    }

    /**
     * Calcule la case située juste devant le portail (vers l'intérieur).
     * C'est cette case qui doit être impérativement vide pour ne pas bloquer le
     * joueur.
     */
    private DiscreteCoordinates getInsideCell(AreaPortals portal) {
        DiscreteCoordinates pos = getArrivalCoordinatesForPortal(portal);

        // Selon le portail, on se décale d'une case vers le centre du jeu
        return switch (portal) {
            case N -> new DiscreteCoordinates(pos.x, pos.y - 1); // Nord -> on descend
            case S -> new DiscreteCoordinates(pos.x, pos.y + 1); // Sud -> on monte
            case W -> new DiscreteCoordinates(pos.x + 1, pos.y); // Ouest -> on va à droite
            case E -> new DiscreteCoordinates(pos.x - 1, pos.y); // Est -> on va à gauche
        };
    }

}
