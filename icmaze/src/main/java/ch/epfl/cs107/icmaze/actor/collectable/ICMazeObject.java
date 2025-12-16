package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

/**
 * ICMazeObject
 * Abstract class representing a collectable object in the ICMaze
 */
public abstract class ICMazeObject extends CollectableAreaEntity {

    private boolean isCollected;

    /**
     * Default ICMazeObject constructor
     * 
     * @param area        (Area): Owner Area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     */
    public ICMazeObject(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        isCollected = false;
    }

    /**
     * Get the current cells occupied by the object
     * 
     * @return (List<DiscreteCoordinates>): The current cells
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }

    /**
     * Check if the object takes cell space
     * 
     * @return (boolean): True if the object takes cell space, false otherwise
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Check if the object is cell interactable
     * 
     * @return (boolean): True if the object is cell interactable, false otherwise
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Check if the object is view interactable
     * 
     * @return (boolean): True if the object is view interactable, false otherwise
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Collect the object
     * Unregisters the object from the area if not already collected
     */
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            getOwnerArea().unregisterActor(this);
        }
    }
}
