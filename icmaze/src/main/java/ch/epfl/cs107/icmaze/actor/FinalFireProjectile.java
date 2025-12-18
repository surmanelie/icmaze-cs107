package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * FinalFireProjectile
 * Adaptation of ICoop Fire class to ICMaze.
 * Represents a fireball projectile.
 */
public class FinalFireProjectile extends Projectile {

    private final Animation animation;

    /**
     * Constructor
     * 
     * @param area        (Area): Owner area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     */
    public FinalFireProjectile(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        // Fallback to "icmaze/magicFireProjectile" as "icoop/fire" is missing
        this.animation = new Animation("icmaze/magicFireProjectile", 4, 1, 1, this, 32, 32, 4, true);
    }

    @Override
    public void update(float dt) {

        if (!isDisplacementOccurs()) {
            move(9);
        }

        super.update(dt);
        animation.update(dt);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    // --- Interaction ---

    public void interactWith(ICMazePlayer player, boolean isCellInteraction) {

        if (isCellInteraction) {
            player.sufferHit();
            stop(); // "la boule de feu se stop lorsqu'elle touche le joueur"
        }
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}
