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

public abstract class Projectile extends ICMazeActor implements Interactor {

    // --- constantes imposées par l'énoncé ---
    protected static final int SPEED = 1;
    protected static final int MAX_RANGE = 7;
    protected static final int DAMAGE = 1;
    private static final int MOVE_DURATION = 4;

    // --- état interne ---
    private int remainingRange = MAX_RANGE;
    private boolean stopped = false;

    private final ProjectileInteractionHandler handler = new ProjectileInteractionHandler();

    public Projectile(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
    }

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

    public void stop() {
        stopped = true;
    }

    // --- position sur la grille ---
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    // --- traversabilité ---
    @Override
    public boolean takeCellSpace() {
        return false; // on peut marcher dessus
    }

    // --- interactions subies ---
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    // --- interactions infligées ---
    @Override
    public boolean wantsCellInteraction() {
        return !stopped;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    // --- visitor obligatoire ---
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    // --- handler interne ---
    private class ProjectileInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction && !stopped) {
                player.sufferHit();
                stop();
                leaveArea();
            }
        }
    }
}
