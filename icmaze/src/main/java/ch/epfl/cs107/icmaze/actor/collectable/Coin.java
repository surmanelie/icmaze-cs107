package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Coin
 * Class representing a collectable Coin in the ICMaze that depends on a logic
 * signal
 */
public class Coin extends ICMazeObject {

    private final Animation animation;

    private final Logic signal;

    /**
     * Default Coin constructor
     * 
     * @param area        (Area): Owner Area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     * @param signal      (Logic): logic signal controlling validity
     */
    public Coin(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal) {
        super(area, orientation, position);
        this.signal = signal;
        this.animation = new Animation("icmaze/coin", 4, 1, 1, this, 16, 16, 4, true);
    }

    /**
     * Update the Coin
     * 
     * @param deltaTime (float): Time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
    }

    /**
     * Draw the Coin if signal is on
     * 
     * @param canvas (Canvas): Canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        if (signal != null && signal.isOn()) {
            animation.draw(canvas);
        }
    }

    /**
     * Check if the Coin takes cell space
     * 
     * @return (boolean): Always false
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Check if the Coin is cell interactable
     * 
     * @return (boolean): True if signal is on, false otherwise
     */
    @Override
    public boolean isCellInteractable() {
        return signal != null && signal.isOn();
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
