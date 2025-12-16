package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;

/**
 * PathFinderEnnemy
 * Abstract class for enemies that use pathfinding.
 */
public abstract class PathFinderEnnemy extends Ennemy implements Interactor {
    private final int perceptionRadius;
    private final static int MOVE_DURATION = 4;

    /**
     * PathFinderEnnemy constructor
     * 
     * @param area             (Area): Owner area
     * @param orientation      (Orientation): Initial orientation
     * @param position         (DiscreteCoordinates): Initial position
     * @param maxHealth        (int): Max health
     * @param perceptionRadius (int): Perception radius in cells
     */
    protected PathFinderEnnemy(Area area,
            Orientation orientation,
            DiscreteCoordinates position,
            int maxHealth,
            int perceptionRadius) {
        super(area, orientation, position, maxHealth);
        this.perceptionRadius = perceptionRadius;
    }

    /**
     * Calculate next orientation for movement
     * 
     * @return (Orientation): The next orientation, or null
     */
    public abstract Orientation getNextOrientation();

    public int getPerceptionRadius() {
        return perceptionRadius;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void updateAlive(float deltaTime) {
        super.updateAlive(deltaTime);

        if (!isDead() && !isDisplacementOccurs()) {
            Orientation next = getNextOrientation();
            if (next != null) {
                orientate(next);
                move(MOVE_DURATION);
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();

        DiscreteCoordinates here = getCurrentMainCellCoordinates();
        int r = perceptionRadius;

        for (int dx = -r; dx <= r; ++dx) {
            for (int dy = -r; dy <= r; ++dy) {
                cells.add(new DiscreteCoordinates(here.x + dx, here.y + dy));
            }
        }
        return cells;
    }

}
