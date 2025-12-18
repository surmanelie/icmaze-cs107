package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.Updatable;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Rock extends AreaEntity implements Interactable, Updatable {

    private Sprite sprite;

    private boolean vanishing = false;
    private boolean hasTakenDamage = false;
    private static final int ANIMATION_DURATION = 24;
    private final Animation vanishAnimation;
    private final Logic signal;

    private final ICMazeArea owner;

    private final Health healthBar = new Health(this, Transform.I.translated(0, 1.0f), 3, false);

    public Rock(ICMazeArea area, Orientation orientation, DiscreteCoordinates coordinates, Logic signal) {
        super(area, orientation, coordinates);
        this.owner = area;
        this.signal = signal;
        sprite = new Sprite("rock.2", 1f, 1f, this);

        vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f),
                ANIMATION_DURATION / 7, false);
    }

    public Rock(ICMazeArea area, DiscreteCoordinates coordinates) {

        this(area, Orientation.DOWN, coordinates, Logic.FALSE);
    }

    private static final float IMMUNITY_DURATION = 1.0f; // même valeur que player/logmonster pour l’instant
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;

    private void triggerImmunity() {
        immune = true;
        blinkTick = 0;
        immunityCd.reset();
    }

    public void weaken() {

        if (vanishing || immune) {
            return;
        }

        healthBar.decrease(1);
        hasTakenDamage = true;

        if (!healthBar.isOn()) {
            vanishing = true;
            owner.onRockDestroyed(getCurrentMainCellCoordinates());

            return;
        }

        triggerImmunity();

    }

    private void handleVanish(float dt) {
        vanishAnimation.update(dt);

        if (vanishAnimation.isCompleted()) {
            vanishAnimation.reset();

            // 1 chance sur 2 de drop un cœur
            if (RandomGenerator.rng.nextBoolean()) {
                getOwnerArea()
                        .registerActor(new Heart(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates()));

            }

            // Retirer le rocher de l’aire
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        boolean visible = !immune || (blinkTick % 2 == 0);

        if (!visible)
            return;

        // Si le signal est ON, on ne dessine rien (le rocher disparait)
        if (signal != null && signal.isOn()) {
            return;
        }

        if (vanishing) {
            vanishAnimation.draw(canvas);
            return;
        }

        sprite.draw(canvas);
        if (hasTakenDamage && healthBar.isOn()) {
            healthBar.draw(canvas);
        }
    }

    @Override
    public void update(float dt) {
        if (vanishing) {
            handleVanish(dt);
            return;
        }

        if (immune) {
            blinkTick++;
            if (immunityCd.ready(dt)) {
                immune = false;
            }
        }
    }

    @Override
    public boolean takeCellSpace() {
        if (signal != null && signal.isOn()) {
            return false;
        }
        return !vanishing;
    }

    @Override
    public boolean isCellInteractable() {
        if (signal != null && signal.isOn()) {
            return false;
        }
        return !vanishing;
    }

    @Override
    public boolean isViewInteractable() {
        if (signal != null && signal.isOn()) {
            return false;
        }
        return !vanishing;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }
}
