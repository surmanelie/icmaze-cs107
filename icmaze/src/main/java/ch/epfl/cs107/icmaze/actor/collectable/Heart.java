package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Heart
 * Class representing a collectable Heart in the ICMaze
 */
public class Heart extends ICMazeObject {

    private final static int ANIMATION_DURATION = 24;
    private Animation animation;

    /**
     * Default Heart constructor
     * 
     * @param area        (Area): Owner Area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     */
    public Heart(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);

        animation = new Animation(
                "icmaze/heart",
                4, 1, 1,
                this,
                16, 16,
                ANIMATION_DURATION / 4,
                true);
    }

    /**
     * Draw the Heart
     * 
     * @param canvas (Canvas): Canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Update the Heart
     * 
     * @param deltaTime (float): Time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
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
