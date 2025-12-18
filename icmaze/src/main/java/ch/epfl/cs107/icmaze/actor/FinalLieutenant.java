package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;

/**
 * FinalLieutenant
 * Adaptation of ICoop darkLord class to ICMaze.
 * Represents the boss assistant.
 */
public class FinalLieutenant extends Ennemy {

    private final LieutenantInteractionHandler handler = new LieutenantInteractionHandler(); // RESTORED
    private OrientedAnimation animation; // RESTORED
    private final OrientedAnimation NormalAnimation; // RESTORED
    private final OrientedAnimation FireAnimation; // RESTORED
    private final Vector anchor = new Vector(-0.5f, 0); // RESTORED
    private final Orientation[] orders = { UP, LEFT, DOWN, RIGHT }; // RESTORED
    // Health bar removed as per user request

    private enum State {
        IDLE, ATTACK, FEUX
    }

    private State currentState = State.IDLE; // RESTORED

    private static final int ANIMATION_DURATION = 24; // RESTORED
    private static final double ORIENTATION_CHANGE_PROBABILITY = 0.4; // RESTORED
    private final int speedFactor = 1; // RESTORED
    private int idleSteps; // RESTORED
    private int FireTime = 20; // RESTORED

    public FinalLieutenant(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 5); // Reduced MAX_LIFE to 5 to make it killable

        // Reverting to "icoop/darkLord" as requested by user code snippet
        // Sized reduced: IDLE 1x1, FIRE 2x2.
        NormalAnimation = new OrientedAnimation("icmaze/darkLord", ANIMATION_DURATION / 3, this, anchor, orders, 3, 1,
                1,
                32, 32, true);
        FireAnimation = new OrientedAnimation("icmaze/darkLord.spell", ANIMATION_DURATION / 3, this, anchor, orders, 3,
                2, 2, 32, 32, true);

        this.animation = NormalAnimation;

    }

    @Override
    protected Animation createDeathAnimation() {
        return new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f), 24 / 7, false);
    }

    private void performRandomMovement() {
        if (RandomGenerator.rng.nextDouble() <= ORIENTATION_CHANGE_PROBABILITY) {
            List<Orientation> otherOrientations = new ArrayList<>();
            for (Orientation o : Orientation.values()) {
                if (o != getOrientation()) {
                    otherOrientations.add(o);
                }
            }
            Orientation newOrientation = otherOrientations.get(RandomGenerator.rng.nextInt(otherOrientations.size()));
            orientate(newOrientation);
        }
        move(ANIMATION_DURATION / speedFactor);
    }

    @Override
    public void updateAlive(float deltaTime) {
        switch (currentState) {
            case IDLE:
                if (idleSteps <= 0) {
                    performRandomMovement();
                    idleSteps = 10;
                } else {
                    idleSteps--;
                }
                break;

            case ATTACK:
                getOwnerArea().registerActor(
                        new FinalFireProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates()));
                currentState = State.FEUX;
                // fallthrough

            case FEUX:
                animation = FireAnimation;
                animation.update(deltaTime);
                FireTime--;

                if (FireTime <= 0) {
                    animation = NormalAnimation;
                    currentState = State.IDLE;
                    animation.update(deltaTime);
                    FireTime = 20;
                }
                break;
        }

        if (currentState == State.IDLE) {
            animation.update(deltaTime);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead()) {
            animation.draw(canvas);

        }
        super.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        if (currentState == State.IDLE) {
            List<DiscreteCoordinates> fieldOfViewCells = new ArrayList<>();
            DiscreteCoordinates coordinates = getCurrentMainCellCoordinates();
            for (int i = 0; i < 32; i++) {
                coordinates = coordinates.jump(getOrientation().toVector());
                fieldOfViewCells.add(coordinates);
            }
            return fieldOfViewCells;
        } else if (currentState == State.ATTACK) {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        } else {
            return Collections.emptyList();
        }
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
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    private class LieutenantInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (currentState == State.IDLE || currentState == State.ATTACK) {
                currentState = State.ATTACK;
                animation = NormalAnimation;
            }
        }
    }
}
