package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Portal extends ICMazeActor implements Interactable {

    private State state;

    private String destinationAreaName;
    private DiscreteCoordinates arrivalCoordinates;
    public static final int NO_KEY_ID = Integer.MIN_VALUE;
    private int keyId;
    private final Sprite invisibleSprite;
    private final Sprite lockedSprite;



    public enum State {
        OPEN,
        LOCKED,
        INVISIBLE }

    public Portal(Area area, Orientation orientation,DiscreteCoordinates position,String destinationAreaName, DiscreteCoordinates arrivalCoordinates, int keyId, State state) {

        super(area, orientation, position);

        this.state = state;
        this.destinationAreaName = destinationAreaName;
        this.arrivalCoordinates = arrivalCoordinates;
        this.keyId = keyId;

        this.invisibleSprite = new Sprite(
                "icmaze/invisibleDoor_" + orientation.ordinal(),
                (orientation.ordinal()+1)%2+1,
                orientation.ordinal()%2+1,
                this);

        this.lockedSprite = new Sprite(
                "icmaze/chained_wood_" + orientation.ordinal(),
                (orientation.ordinal()+1)%2+1,
                orientation.ordinal()%2+1,
                this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        switch(state){
           case INVISIBLE : invisibleSprite.draw(canvas);
            break;
           case LOCKED : lockedSprite.draw(canvas);
            break;
           case OPEN :
                    /* draw nothing */
            break;

            default: break;
        }

       // super.draw(canvas);
    }

    @Override
    public boolean takeCellSpace() {
        //return state != State.OPEN;
        //on fait ce changement pour qu'on puisse passer sur un portail même s'il est invisible et paas que s'il est open
        return state == State.LOCKED;
    }
    @Override
    public boolean isViewInteractable() {
        return true; }

    @Override
    public boolean isCellInteractable() {
        return state == State.OPEN;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord, coord.jump(new
                Vector((getOrientation().ordinal()+1)%2,
                getOrientation().ordinal()%2)));
    }

    public State getState() {
        return state;
    }

//    public void open() {
//
//        if (state == State.LOCKED) {
//            state = State.OPEN;
//        }
//    }

    public String getDestinationAreaName() {
        return destinationAreaName;
    }

    public DiscreteCoordinates getArrivalCoordinates() {
        return arrivalCoordinates;
    }

    public void setDestinationAreaName(String destinationAreaName) {
        this.destinationAreaName = destinationAreaName;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this,isCellInteraction);
        }
    }
}
