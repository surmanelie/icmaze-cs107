package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
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

    private String northDestination ;
    private String southDestination ;
    private String westDestination;
    private String eastDestination;

    private  Portal.State northState = Portal.State.INVISIBLE;
    private  Portal.State southState = Portal.State.INVISIBLE;
    private  Portal.State eastState = Portal.State.INVISIBLE;
    private  Portal.State weststate = Portal.State.INVISIBLE;


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

    public abstract DiscreteCoordinates getplayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem){
        if(!super.begin(window,fileSystem)) { // on ne commence pas si c'est pas bon
            return false; }

        setBehavior(new ICMazeBehavior(window, behaviorName)); // on installe le behavior adapté à l'air choisie
        registerActor(new Background(this, behaviorName)); // on enregistre le background associé à l'aire
        createArea();
        createPortals();

        return true; // car tout s'est bien passé
    }

    protected  abstract void createArea();



    protected void setNorthState(Portal.State state) {
       this.northState = state;
    }

    protected void setSouthState(Portal.State state) {
        this.southState = state;

    }

    protected void setEastState(Portal.State state) {
        this.eastState = state;

    }

    protected void setWestState(Portal.State state) {
        this.weststate = state;

    }


    // tous les prochains sont changés pour ne pas toucher aux objets Portal

    protected void setNorthDestination(String destination){
        this.northDestination = destination;
        //N.setDestinationAreaName(destination);
    }
    protected void setSouthDestination(String destination){
        this.southDestination = destination;
        //S.setDestinationAreaName(destination);
    }
    protected void setWestDestination(String destination){
        this.westDestination = destination;
        //W.setDestinationAreaName(destination);
    }
    protected void setEastDestination(String destination){
        this.eastDestination = destination;
        //c'est E.setDestinationAreaName(destination);
    }



    public String getBehaviorName() {
        return behaviorName;
    }

    protected void createPortals() {// modifier cordonne arrivee

        // Portail Nord
        N = new Portal( this, AreaPortals.N.getOrientation().opposite(),  new DiscreteCoordinates(size / 2, size + 1),northDestination,new DiscreteCoordinates(size/2,0),0, northState);

        // Portail Sud
        S = new Portal( this, AreaPortals.S.getOrientation().opposite(), new DiscreteCoordinates(size / 2, 0),southDestination ,new DiscreteCoordinates(size / 2, size+1), 0, southState);

        // Portail Ouest
        W = new Portal( this, AreaPortals.W.getOrientation().opposite(), new DiscreteCoordinates(0, size / 2), westDestination,new DiscreteCoordinates(size+1, size / 2) ,0,weststate);
        // Portail Est
        E = new Portal( this, AreaPortals.E.getOrientation().opposite(), new DiscreteCoordinates(size + 1, size / 2), eastDestination,new DiscreteCoordinates(0, size / 2) , 0, eastState);

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

}
