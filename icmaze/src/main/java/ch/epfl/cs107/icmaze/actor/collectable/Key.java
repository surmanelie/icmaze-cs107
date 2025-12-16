package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Key
 * Class representing a collectable Key in the ICMaze
 */
public class Key extends ICMazeObject {

    private final int id;
    public final Sprite sprite;
    private boolean collected = false;

    /**
     * Default Key constructor
     * 
     * @param area        (Area): Owner Area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     * @param id          (int): The Key identifier
     */
    public Key(Area area, Orientation orientation, DiscreteCoordinates position, int id) {
        super(area, orientation, position);
        this.id = id;
        this.sprite = new Sprite("icmaze/key", 1f, 1f, this);
    }

    /**
     * Get the Key id
     * 
     * @return (int): The id
     */
    public int getId() {
        return id;
    }

    /**
     * Collect the Key
     * Updates internal collected state
     */
    @Override
    public void collect() {
        super.collect();
        collected = true;
    }

    /**
     * Check if the Key is collected
     * 
     * @return (boolean): True if collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Draw the Key
     * 
     * @param canvas (Canvas): Canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * Accept interaction from a visitor
     * 
     * @param v                 (AreaInteractionVisitor): The visitor
     * @param isCellInteraction (boolean): True if interaction is cell-based
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }
}
