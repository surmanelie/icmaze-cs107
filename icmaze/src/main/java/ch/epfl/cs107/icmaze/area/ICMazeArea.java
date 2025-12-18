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

public abstract class ICMazeArea extends Area implements DialogHandler, Logic {
    // ... in existing class body
    @Override
    public boolean isOn() {
        return validationSignal.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    public void setValidationSignal(Logic signal) {
        this.validationSignal = signal;
    }

    // getValidationSignal removed
    private final String behaviorName;
    private Portal W;
    private Portal S;
    private Portal E;
    private Portal N;
    private final int size;

    private ICMaze game;

    // private static final float DEFAULT_SCALE_FACTOR = 11.f;
    private static final float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    private static final float MAXIMUM_SCALE = 30f;

    @Override
    public void publish(Dialog dialog) {
        if (game != null) {
            game.publish(dialog);
        }
    }

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

        int effectiveSize = Math.max(getWidth(), getHeight());

        return Math.min(effectiveSize * DYNAMIC_SCALE_MULTIPLIER, MAXIMUM_SCALE);
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

        return true;
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

    public void setNorthDestination(String destination, int nextSize) {
        this.N.setDestinationArea(destination);
        this.N.setArrivalCoordinates(new DiscreteCoordinates(nextSize / 2, 1));
    }

    public void setSouthDestination(String destination, int nextSize) {
        this.S.setDestinationArea(destination);
        this.S.setArrivalCoordinates(new DiscreteCoordinates(nextSize / 2, nextSize));
    }

    public void setWestDestination(String destination, int nextSize) {
        this.W.setDestinationArea(destination);
        this.W.setArrivalCoordinates(new DiscreteCoordinates(nextSize, nextSize / 2));

    }

    public void setEastDestination(String destination, int nextSize) {
        this.E.setDestinationArea(destination);
        this.E.setArrivalCoordinates(new DiscreteCoordinates(1, nextSize / 2));
    }

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

    public int getSize() {
        return size;
    }

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

    public Queue<Orientation> getShortestPath(DiscreteCoordinates from, DiscreteCoordinates to) {
        return graph.shortestPath(from, to);
    }

    public void setGame(ICMaze game) {
        this.game = game;
    }

    public void incrementMonsterKillCount() {
        if (game != null) {
            game.incrementMonsterKillCount();
        }
    }

    public int getMonsterKillCount() {
        if (game != null) {
            return game.getMonsterKillCount();
        }
        return 0;
    }

    public void requestReset() {
        if (game != null)
            game.resetCurrentArea();
    }

    private Logic validationSignal = Logic.FALSE;

    public void onRockDestroyed(DiscreteCoordinates cell) {

    }

    public java.util.List<DiscreteCoordinates> getPortalCells() {
        java.util.List<DiscreteCoordinates> cells = new java.util.ArrayList<>();
        if (N != null)
            cells.addAll(N.getCurrentCells());
        if (S != null)
            cells.addAll(S.getCurrentCells());
        if (E != null)
            cells.addAll(E.getCurrentCells());
        if (W != null)
            cells.addAll(W.getCurrentCells());
        return cells;
    }

}
