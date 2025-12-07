package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public abstract class AireLabyrinthique extends ICMazeArea {
    private AreaPortals portalEnter; // il permet d entrer dans  le labyrinthe associé
    private AreaPortals portalExit;
    protected int keyId;
    public final static int keyIdL1 = Integer.MAX_VALUE;
    public final static int keyIdL2= Integer.MAX_VALUE -1;
    public final static int keyIdL3= Integer.MAX_VALUE -2;
    public final static int keyIdL4 = Integer.MAX_VALUE-3;
    //private final String gridName;



    /** Matrice du labyrinthe : 0 = chemin, 1 = mur */
    protected int[][] mazeGrid;

    private int difficulty;

    public AireLabyrinthique(String behaviorName, int size, AreaPortals portalEnter, AreaPortals portalExit, int Keyid, int difficulty) {
        super(behaviorName, size);
        this.portalEnter = portalEnter;
        this.portalExit = portalExit;
        this.keyId = Keyid;
        this.difficulty = difficulty;
//        this.gridName = gridName;
    }

    public int getKeyId() {
        return keyId;
    }

    @Override
    protected void createArea() {
        mazeGrid = MazeGenerator.createMaze(size, size, difficulty);
        MazeGenerator.printMaze(mazeGrid, getEntryArrivalCoordinates(), getExitArrivalCoordinates());
        placeRocks();


    }

    /** Placement des rochers en fonction de mazeGrid */
    private void placeRocks() {

        DiscreteCoordinates entry = getEntryArrivalCoordinates();
        DiscreteCoordinates exit  = getExitArrivalCoordinates();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                if (mazeGrid[y][x] == 1) {

                    DiscreteCoordinates pos = new DiscreteCoordinates(x+1,y+1);
                    registerActor(new Rock(this, pos));

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

}
