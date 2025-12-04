package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;

public abstract class AireLabyrinthique extends ICMazeArea {
    private AreaPortals portalEnter; // il permet d entrer dans  le labyrinthe associé
    private AreaPortals portalExit;
    private  int keyId;
    public final static int keyIdL1 = Integer.MAX_VALUE;
    public final static int keyIdL2= Integer.MAX_VALUE -1;
    public final static int keyIdL3= Integer.MAX_VALUE -2;
    public final static int keyIdL4 = Integer.MAX_VALUE-3;

// on cree un attricut qui nous permet de stocker la key pour sortir de l aire souhaite
    private int difficulty;

    public AireLabyrinthique(String behaviorName, int size, AreaPortals portalEnter, AreaPortals portalExit, int Keyid, int difficulty) {
        super(behaviorName, size);
        this.portalEnter = portalEnter;
        this.portalExit = portalExit;
        this.keyId = Keyid;
        this.difficulty = difficulty;
    }

    public int getKeyId() {
        return keyId;
    }
}
