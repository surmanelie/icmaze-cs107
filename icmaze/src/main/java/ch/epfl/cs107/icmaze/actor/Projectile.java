package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

/**
 * Projectile
 * Abstract class representing a projectile in the ICMaze
 */
public abstract class Projectile extends ICMazeActor implements Interactor {

    private static final int SPEED = 1;
    private static final int MAX_RANGE = 7;
    private static final int DAMAGE = 1;
    private static final int MOVE_DURATION = 4;

    private int remainingRange = MAX_RANGE;
    private boolean stopped = false;

    private final ProjectileInteractionHandler handler = new ProjectileInteractionHandler();

    /**
     * Default Projectile constructor
     * 
     * @param owner       (Area): Owner Area, not null
     * @param orientation (Orientation): Initial orientation of the projectile, not
     *                    null
     * @param coordinates (DiscreteCoordinates): Initial position, not null
     */
    public Projectile(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
    }

    /**
     * Update the projectile
     * 
     * @param dt (float): Delta time
     */
    @Override
    public void update(float dt) {
        super.update(dt);

        if (stopped)
            return;

        if (!isDisplacementOccurs()) {
            if (remainingRange > 0) {
                move(MOVE_DURATION / SPEED);
                remainingRange--;
            } else {
                stop();
                leaveArea();
            }
        }
    }

    /**
     * Stop the projectile
     */
    public void stop() {
        stopped = true;
    }

    /**
     * Get the current cells occupied by the projectile
     * 
     * @return (List<DiscreteCoordinates>): List of occupied cells
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Check if the projectile takes cell space
     * 
     * @return (boolean): false (walkable)
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Check if the projectile is interactable via cells
     * 
     * @return (boolean): false
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * Check if the projectile is interactable via view
     * 
     * @return (boolean): false
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Check if the projectile wants cell interactions
     * 
     * @return (boolean): true if not stopped
     */
    @Override
    public boolean wantsCellInteraction() {
        return !stopped;
    }

    /**
     * Check if the projectile wants view interactions
     * 
     * @return (boolean): false
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Get the cells in the field of view
     * 
     * @return (List<DiscreteCoordinates>): List of cells in field of view
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * Interact with another interactive entity
     * 
     * @param other             (Interactable): The other entity
     * @param isCellInteraction (boolean): True if cell interaction
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * Accept interaction from a visitor
     * 
     * @param v                 (AreaInteractionVisitor): The visitor
     * @param isCellInteraction (boolean): True if cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * ProjectileInteractionHandler
     * Internal handler for projectile interactions
     */
    private class ProjectileInteractionHandler implements ICMazeInteractionVisitor {

        /**
         * Interact with a player
         * 
         * @param player            (ICMazePlayer): The player
         * @param isCellInteraction (boolean): True if cell interaction
         */
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction && !stopped) {
                leaveArea();
                player.sufferHit();
                stop();
            }
        }
    }
}
