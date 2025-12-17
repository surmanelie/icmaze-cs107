package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Ennemy
 * Abstract class representing an enemy entity in the game.
 */
public abstract class Ennemy extends ICMazeActor implements Interactor, Interactable {

    // --- Attributes ---
    private int currentHealth;
    private final int maxHealth;

    private enum State {
        ALIVE,
        DYING,
        DEAD
    }

    private State state;

    private final Animation deathAnimation;

    /**
     * Ennemy constructor
     * 
     * @param area        (Area): Owner area, not null
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     * @param maxHealth   (int): Maximum health points
     */
    protected Ennemy(Area area,
            Orientation orientation,
            DiscreteCoordinates position,
            int maxHealth) {
        super(area, orientation, position);

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.state = State.ALIVE;

        this.deathAnimation = createDeathAnimation();
    }

    /**
     * Create the death animation for this enemy
     * 
     * @return (Animation): The death animation
     */
    protected abstract Animation createDeathAnimation();

    // --- Accessors ---

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    // --- Health Logic ---

    /**
     * Inflict damage to the enemy.
     * 
     * @param amount (int): Amount of damage
     */
    public void loseHealth(int amount) {
        if (state != State.ALIVE) {
            return;
        }

        currentHealth -= amount;

        if (currentHealth <= 0) {
            currentHealth = 0;
            startDying();
        }
    }

    /**
     * Start the dying process
     */
    private void startDying() {
        state = State.DYING;
        deathAnimation.reset();
    }

    // --- Physics ---

    @Override
    public boolean takeCellSpace() {
        return state == State.ALIVE;
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
    public void update(float deltaTime) {
        super.update(deltaTime);

        switch (state) {
            case ALIVE:
                updateAlive(deltaTime);
                break;

            case DYING:
                deathAnimation.update(deltaTime);
                if (deathAnimation.isCompleted()) {
                    getOwnerArea().unregisterActor(this);
                    state = State.DEAD;
                }
                break;

            case DEAD:
                break;
        }
    }

    /**
     * Behavior when alive.
     * Subclasses should override this instead of update for normal behavior.
     * 
     * @param deltaTime (float): Delta time
     */
    public void updateAlive(float deltaTime) {
    }

    @Override
    public void draw(Canvas canvas) {
        if (state == State.DYING) {
            deathAnimation.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }
}
