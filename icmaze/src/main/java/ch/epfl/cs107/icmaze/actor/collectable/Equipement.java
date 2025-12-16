package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Equipement
 * Abstract class representing a collectable Equipment in the ICMaze
 */
public abstract class Equipement extends ICMazeObject {

    private Sprite sprite;

    /**
     * Default Equipement constructor
     * 
     * @param area        (Area): Owner Area. Not null
     * @param orientation (Orientation): Initial orientation. Not null
     * @param position    (DiscreteCoordinates): Initial position. Not null
     */
    public Equipement(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }

    /**
     * Draw the Equipment
     * 
     * @param canvas (Canvas): Canvas to draw on. Not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (sprite != null) {
            sprite.draw(canvas);
        }
    }

    /**
     * Set the Equipment sprite
     * 
     * @param sprite (Sprite): The sprite to set
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
