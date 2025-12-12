package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Ennemy extends ICMazeActor implements Interactor, Interactable {

    // --- Attributs privés (encapsulation stricte) ---
    private int currentHealth;
    private final int maxHealth;

    private enum State { ALIVE, DYING, DEAD }
    private State state;

    private final Animation deathAnimation;

    // --- Constructeur ---
    protected Ennemy(Area area,
                     Orientation orientation,
                     DiscreteCoordinates position,
                     int maxHealth) {
        super(area, orientation, position);

        // Le sujet dit : on ne fixe pas le max ici => on le reçoit en paramètre
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.state = State.ALIVE;

        this.deathAnimation = createDeathAnimation();
    }

    /** Chaque type d'ennemi fournira sa propre animation de mort. */
    protected abstract Animation createDeathAnimation();


    // --- Accesseurs (lecture seule de l'extérieur) ---
    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }


    // --- Perte de points de vie (API officielle) ---
    /**
     * Méthode à utiliser partout pour infliger des dégâts à l'ennemi.
     * Elle applique la règle : mort si PV <= 0 + animation + disparition.
     */
    public void loseHealth(int amount) {
        if (state != State.ALIVE) {
            return; // déjà en train de mourir ou mort
        }

        currentHealth -= amount;

        if (currentHealth <= 0) {
            currentHealth = 0;
            startDying();
        }
    }

    /** Démarre le processus "animation de mort + future disparition". */
    private void startDying() {
        state = State.DYING;
        deathAnimation.reset();
    }


    // --- Contraintes de déplacements / collisions ---
    @Override
    public boolean takeCellSpace() {
        // On bloque la case tant qu'on est en vie
        return state == State.ALIVE;
    }

    // --- Interactions : par défaut, tout est autorisé (distance + contact) ---
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


    // --- Cycle de vie (update / animation de mort / disparition) ---
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
                // normalement plus rien à faire
                break;
        }
    }

    /**
     * Comportement normal de l'ennemi (déplacement, IA...) quand il est vivant.
     * Les sous-classes surchargent cette méthode, pas les champs.
     */
    public void updateAlive(float deltaTime) {
        // par défaut : rien
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

