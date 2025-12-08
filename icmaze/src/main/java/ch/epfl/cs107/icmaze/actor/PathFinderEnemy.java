package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;

public abstract class PathFinderEnemy  extends Enemy implements Interactor {
    private final int perceptionRadius;
    private final static int MOVE_DURATION = 4; // ou autre valeur


    protected PathFinderEnemy(Area area,
                              Orientation orientation,
                              DiscreteCoordinates position,
                              int maxHealth,
                              int perceptionRadius) {
        super(area, orientation, position, maxHealth);
        this.perceptionRadius = perceptionRadius;
    }

    public abstract Orientation getNextOrientation();

    public int getPerceptionRadius() {
        return perceptionRadius;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false; // pas demandeur de contact
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;  // demandeur à distance
    }
    @Override
    public void  updateAlive(float deltaTime) {
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
