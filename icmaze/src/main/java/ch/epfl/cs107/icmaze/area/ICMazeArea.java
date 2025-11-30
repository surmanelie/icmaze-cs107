package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public abstract class ICMazeArea extends Area {
    private final String behaviorName;
    private Portal W;
    private Portal S;
    private Portal E;
    private Portal N;
    protected final int size;



    //extends Area ?

    public ICMazeArea(String behaviorName, int size ) {
        // validation: est -ce que c'est bien ça la modif à faire dans le 2.2 par rapport au tutoriel pour avoir plusieurs noms
        super();
        this.behaviorName = behaviorName;
        this.size = size;
    }

    @Override
    public float getCameraScaleFactor(){
        return 20f;
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem){
        super.begin(window,fileSystem);
        setBehavior(new ICMazeBehavior(window, behaviorName));
        createArea();
        createPortals();
        return true;
    }

    protected void createArea(){
        registerActor(new Background(this, behaviorName));

    }




    protected void createPortals() {

        // Portail Nord
        N = new Portal(
                this,
                Orientation.DOWN,  // le sprite regarde vers la carte
                new DiscreteCoordinates(size / 2, size + 1),
                null,
                null,
                Portal.NO_KEY_ID
        );

        // Portail Sud
        S = new Portal(
                this,
                Orientation.UP,
                new DiscreteCoordinates(size / 2, 0),
                null,
                null,
                Portal.NO_KEY_ID
        );

        // Portail Ouest
        W = new Portal(
                this,
                Orientation.RIGHT,
                new DiscreteCoordinates(0, size / 2),
                null,
                null,
                Portal.NO_KEY_ID
        );

        // Portail Est
        E = new Portal(
                this,
                Orientation.LEFT,
                new DiscreteCoordinates(size + 1, size / 2),
                null,
                null,
                Portal.NO_KEY_ID
        );

        // Enregistrer les portails comme acteurs
        registerActor(N);
        registerActor(S);
        registerActor(E);
        registerActor(W);
    }

    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);

        private final Orientation orientation;

        AreaPortals(Orientation orientation) {
            this.orientation = orientation;
        }

        public Orientation getOrientation() {
            return orientation;
        }
    }
    public DiscreteCoordinates getArrivalCoordinates(AreaPortals portal) {
        return switch (portal) {
            case N -> new DiscreteCoordinates(size/2 + 1, size);
            case S -> new DiscreteCoordinates(size/2 + 1, 1);
            case W -> new DiscreteCoordinates(1, size/2 + 1);
            case E -> new DiscreteCoordinates(size, size/2 + 1);
        };
    }





}
