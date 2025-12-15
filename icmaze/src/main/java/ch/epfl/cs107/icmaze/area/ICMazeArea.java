package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;
import ch.epfl.cs107.play.signal.logic.Logic;

import ch.epfl.cs107.icmaze.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Dialog;

import java.util.Queue;

public abstract class ICMazeArea extends Area implements DialogHandler {
    private final String behaviorName;
    private Portal W;
    private Portal S;
    private Portal E;
    private Portal N;
    protected final int size;

    private ICMaze game;

    @Override
    public void publish(Dialog dialog) {
        if (game != null) {
            game.publish(dialog);
        }
    }

    // ... imports ...

    public ICMazeArea(String behaviorName, int size) {
        // validation: est -ce que c'est bien ça la modif à faire dans le 2.2 par
        // rapport au tutoriel pour avoir plusieurs noms
        super();
        this.behaviorName = behaviorName;
        this.size = size;
        createPortals();
    }

    @Override
    public float getCameraScaleFactor() {
        return (float) Math.min(getSize() * 1.375, 30);
    }

    public abstract DiscreteCoordinates getplayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (!super.begin(window, fileSystem)) { // on ne commence pas si c'est pas bon
            return false;
        }

        setBehavior(new ICMazeBehavior(window, behaviorName)); // on installe le behavior adapté à l'air choisie
        registerActor(new Background(this, behaviorName)); // on enregistre le background associé à l'aire
        createArea();
        // Enregistrer les portails comme acteurs
        registerActor(N);
        registerActor(S);
        registerActor(E);
        registerActor(W);

        return true; // car tout s'est bien passé
    }

    protected abstract void createArea();

    public void setNorthState(Portal.State state) {
        this.N.setState(state);
    }

    public void setSouthState(Portal.State state) {
        this.S.setState(state);
    }

    public void setEastState(Portal.State state) {
        this.E.setState(state);
    }

    public void setWestState(Portal.State state) {
        this.W.setState(state);
    }

    // tous les prochains sont changés pour ne pas toucher aux objets Portal

    public void setNorthDestination(String destination, int nextSize) {
        // this.northDestination = destination;
        this.N.setDestinationArea(destination);
        this.N.setArrivalCoordinates(new DiscreteCoordinates(nextSize / 2, 1));
    }

    public void setSouthDestination(String destination, int nextSize) {
        // this.southDestination = destination;
        this.S.setDestinationArea(destination);
        this.S.setArrivalCoordinates(new DiscreteCoordinates(nextSize / 2, nextSize));
    }

    public void setWestDestination(String destination, int nextSize) {
        // this.westDestination = destination;
        this.W.setDestinationArea(destination);
        this.W.setArrivalCoordinates(new DiscreteCoordinates(nextSize, nextSize / 2));

    }

    public void setEastDestination(String destination, int nextSize) {
        // this.eastDestination = destination;
        this.E.setDestinationArea(destination);
        this.E.setArrivalCoordinates(new DiscreteCoordinates(1, nextSize / 2));
    }

    // public void setDestination(int sizeNext){
    // N.setArrivalCoordinates(new DiscreteCoordinates(sizeNext/2, 1));
    // S.setArrivalCoordinates(new DiscreteCoordinates(sizeNext/2, sizeNext));
    // W.setArrivalCoordinates(new DiscreteCoordinates(sizeNext, sizeNext/2));
    // E.setArrivalCoordinates(new DiscreteCoordinates(1,sizeNext/2));
    // }

    public void setNorthKeyId(int id) {
        this.N.setKeyId(id);
    }

    public void setSouthKeyId(int id) {
        this.S.setKeyId(id);
    }

    public void setEastKeyId(int id) {
        this.E.setKeyId(id);
    }

    public void setWestKeyId(int id) {
        this.W.setKeyId(id);
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    // public abstract DiscreteCoordinates startingCoordinates(int sizeStart);
    //
    // public abstract DiscreteCoordinates arrivalCoordinates(int sizeArrival);

    public abstract int getSize();

    // public void setDestination(DiscreteCoordinates coord){
    // N.setArrivalCoordinates(coord.jump(0,1));
    // }

    // ici l'objectif c'est de dire où tu vas arriver par rapport à la tu pars
    // et du coup ici ce qui est bien c'est que on prend en paramètre la size de
    // l'aire d'après
    // ce qui permet d'arriver à des coordonnées qui correspondent bien à l'aire
    // d'arrivée
    // par exemple si je prends le portail Nord, alors le but c'est d'arriver par le
    // portail
    // sud, donc on prend la coordonnée du portail sud, et on lui rajoute 1 en y.
    // du coup s'il prend en sud, il arrive par le nord mais en y-1
    // s'il prend le portail ouest, alors il arrive en est x-1
    // s'il prend le poratil est, alors il arrive par l'ouest du porchain en x+1

    protected void createPortals() {// modifier cordonne arrivee

        // Portail Nord
        N = new Portal(this, AreaPortals.N.getOrientation().opposite(),
                new DiscreteCoordinates(getSize() / 2, getSize() + 1), null, Portal.NO_KEY_ID, Portal.State.INVISIBLE);

        // Portail Sud
        S = new Portal(this, AreaPortals.S.getOrientation().opposite(), new DiscreteCoordinates(getSize() / 2, 0), null,
                Portal.NO_KEY_ID, Portal.State.INVISIBLE);

        // Portail Ouest
        W = new Portal(this, AreaPortals.W.getOrientation().opposite(), new DiscreteCoordinates(0, getSize() / 2), null,
                Portal.NO_KEY_ID, Portal.State.INVISIBLE);

        // Portail Est
        E = new Portal(this, AreaPortals.E.getOrientation().opposite(),
                new DiscreteCoordinates(getSize() + 1, getSize() / 2), null, Portal.NO_KEY_ID, Portal.State.INVISIBLE);

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

    protected AreaGraph graph = new AreaGraph();

    // le get ci dessous est intrusif
    // public AreaGraph getGraph(){
    // return graph;
    // }

    public Queue<Orientation> getShortestPath(DiscreteCoordinates from, DiscreteCoordinates to) {
        return graph.shortestPath(from, to);
    }

    // // ce qui arrive je suis pas sûr
    //
    // private ICMaze game;
    //
    public void setGame(ICMaze game) {
        this.game = game;
    }

    public void requestReset() {
        if (game != null)
            game.resetCurrentArea();
    }

    // public ICMaze getGame() {
    // return game;
    // }

    private Logic validationSignal = Logic.FALSE;

    public void setValidationSignal(Logic signal) {
        this.validationSignal = signal;
    }

    protected Logic getValidationSignal() {
        return validationSignal;
    }

}
