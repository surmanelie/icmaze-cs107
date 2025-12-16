package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * FireProjectile
 * A specific type of projectile represented by a fire animation.
 */
public class FireProjectile extends Projectile {

    private static final int ANIMATION_DURATION = 12;
    private final Animation animation;

    /**
     * Default FireProjectile constructor
     * 
     * @param owner       (Area): Owner Area, not null
     * @param orientation (Orientation): Initial orientation of the projectile, not
     *                    null
     * @param coordinates (DiscreteCoordinates): Initial position, not null
     */
    public FireProjectile(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);

        animation = new Animation(
                "icmaze/magicFireProjectile",
                4, 1, 1,
                this,
                32, 32,
                ANIMATION_DURATION / 4,
                true);
    }

    /**
     * Update the projectile and its animation
     * 
     * @param dt (float): Delta time
     */
    @Override
    public void update(float dt) {
        super.update(dt);
        animation.update(dt);
    }

    /**
     * Draw the projectile's animation
     * 
     * @param canvas (Canvas): The canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }
}
