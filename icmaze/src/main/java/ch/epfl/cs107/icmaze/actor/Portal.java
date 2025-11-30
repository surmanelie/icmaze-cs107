package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Portal extends ICMazeActor implements Interactable {
    public enum State {OPEN,LOCKED, INVISIBLE }
    private State state;

    private String destinationAreaName;
    private DiscreteCoordinates arrivalCoordinates;
    public static final int NO_KEY_ID = Integer.MIN_VALUE;
    private int keyId;
    private final Sprite invisibleSprite;
    private final Sprite lockedSprite;




    public Portal(Area owner,
                  Orientation orientation,
                  DiscreteCoordinates position,
                  String destinationAreaName,
                  DiscreteCoordinates arrivalCoordinates,
                  int keyId) {

        super(owner, orientation, position);

        this.state = State.INVISIBLE;
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
        switch(state){
            case INVISIBLE : invisibleSprite.draw(canvas);
            case LOCKED : lockedSprite.draw(canvas);
            case OPEN :{ /* draw nothing */ }
        }

        super.draw(canvas);
    }
    @Override
    public boolean takeCellSpace() {
        return state != State.OPEN;
    }
    @Override
    public boolean isViewInteractable() { return true; }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord, coord.jump(new
                Vector((getOrientation().ordinal()+1)%2,
                getOrientation().ordinal()%2)));
    }
}
