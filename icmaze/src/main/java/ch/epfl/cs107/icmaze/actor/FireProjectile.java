package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class FireProjectile extends Projectile {

    private static final int ANIMATION_DURATION = 12;
    private final Animation animation;

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

    @Override
    public void update(float dt) {
        super.update(dt);
        animation.update(dt);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }
}
