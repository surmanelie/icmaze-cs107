package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

/**
 * ICMazeActor
 * Abstract class representing an actor in the ICMaze game.
 */
public abstract class ICMazeActor extends MovableAreaEntity implements Interactable {

    /**
     * ICMazeActor constructor
     * 
     * @param owner       (Area): Owner area, not null
     * @param orientation (Orientation): Initial orientation of the actor, not null
     * @param coordinates (DiscreteCoordinates): Initial coordinates of the actor,
     *                    not null
     */
    public ICMazeActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Unregister the actor from the current area
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Register the actor to a new area
     * 
     * @param area     (Area): The new area
     * @param position (DiscreteCoordinates): The new position
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
        area.registerActor(this);
    }

}
