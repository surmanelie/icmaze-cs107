package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.LinkedList;
import java.util.Queue;

/**
 * LogMonster
 * A specific monster that looks like a log.
 * It has different states: Sleeping, Random movement, and Targeting player.
 */
public class LogMonster extends PathFinderEnnemy {

    private static final int MAX_HEALTH = 3;
    private static final int PERCEPTION_RADIUS = 5;

    private final double pTransition;

    public enum State {
        SLEEPING,
        RANDOM,
        TARGETING
    }

    private State state;

    private DiscreteCoordinates lastKnowPlayerPosition;

    private Orientation plannedOrientation;

    private final Cooldown reorientCooldown;
    private final Cooldown stateCooldown;

    private static final int DEATH_ANIMATION_DURATION = 24;
    private static final int ANIMATION_DURATION = 12;

    private final OrientedAnimation targetingAnimation;
    private final OrientedAnimation randomAnimation;
    private final OrientedAnimation sleepingAnimation;

    private Path graphicPath;

    private static final float IMMUNITY_DURATION = 1.0f;
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;
    private boolean hasTakenDamage = false;
    private boolean killNotified = false;

    private final Logic signal;

    private final Health healthBar = new Health(this, Transform.I.translated(0, 1.75f), MAX_HEALTH, false);

    private void triggerImmunity() {
        immune = true;
        blinkTick = 0;
        immunityCd.reset();
    }

    private final LogMonsterInteractionHandler handler = new LogMonsterInteractionHandler();

    /**
     * LogMonster constructor
     * 
     * @param area         (Area): Owner area
     * @param orientation  (Orientation): Initial orientation
     * @param position     (DiscreteCoordinates): Initial position
     * @param initialState (State): Initial state
     * @param signal       (Logic): Signal to control sleeping state
     */
    /**
     * LogMonster constructor
     * 
     * @param area         (Area): Owner area
     * @param orientation  (Orientation): Initial orientation
     * @param position     (DiscreteCoordinates): Initial position
     * @param initialState (State): Initial state
     * @param signal       (Logic): Signal to control sleeping state
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, State initialState,
            Logic signal, int difficulty) {

        super(area, orientation, position, MAX_HEALTH, PERCEPTION_RADIUS);
        this.state = initialState;
        this.signal = signal;

        this.reorientCooldown = new Cooldown(0.75f);
        this.stateCooldown = new Cooldown(3.0f);

        // difficulty injected
        this.pTransition = (double) Difficulty.HARDEST / (double) difficulty;

        Vector anchor = new Vector(-0.5f, 0.25f);

        Orientation[] ordersTargeting = {
                Orientation.DOWN,
                Orientation.RIGHT,
                Orientation.UP,
                Orientation.LEFT
        };

        targetingAnimation = new OrientedAnimation("icmaze/logMonster", ANIMATION_DURATION / 3, this, anchor,
                ordersTargeting, 4, 2, 2, 32, 32, true);

        Orientation[] ordersRandom = {
                Orientation.DOWN,
                Orientation.UP,
                Orientation.RIGHT,
                Orientation.LEFT
        };

        randomAnimation = new OrientedAnimation("icmaze/logMonster_random", ANIMATION_DURATION / 3, this, anchor,
                ordersRandom, 4, 2, 2, 32, 32, true);

        Orientation[] ordersSleeping = {
                Orientation.DOWN,
                Orientation.LEFT,
                Orientation.UP,
                Orientation.RIGHT
        };
        sleepingAnimation = new OrientedAnimation("icmaze/logMonster.sleeping", ANIMATION_DURATION / 3, this, anchor,
                ordersSleeping, 1, 2, 2, 32, 32, true);

    }

    @Override
    protected Animation createDeathAnimation() {
        return new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f),
                DEATH_ANIMATION_DURATION / 7, false);

    }

    private Orientation leftof(Orientation o) {
        return switch (o) {
            case UP -> Orientation.LEFT;
            case LEFT -> Orientation.DOWN;
            case DOWN -> Orientation.RIGHT;
            case RIGHT -> Orientation.UP;
            default -> o;
        };
    }

    @Override
    public void updateAlive(float deltaTime) {
        if (immune) {
            blinkTick++;
            if (immunityCd.ready(deltaTime)) {
                immune = false;
            }
        }
        // Gestion prioritaire du signal (Victoire)
        if (signal != null && signal.isOn()) {
            if (state != State.SLEEPING) {
                state = State.SLEEPING;
                sleepingAnimation.reset();
                graphicPath = null;
            }
            sleepingAnimation.update(deltaTime);

            return;
        }

        boolean canReorient = reorientCooldown.ready(deltaTime);
        boolean canChangeState = stateCooldown.ready(deltaTime);

        var rng = RandomGenerator.rng;

        plannedOrientation = null;

        switch (state) {

            case SLEEPING -> {
                if (canReorient) {
                    orientate(leftof(getOrientation()));
                }

                if (canChangeState && rng.nextDouble() < pTransition) {
                    state = State.RANDOM;
                    randomAnimation.reset();
                }
            }

            case RANDOM -> {
                if (canReorient) {
                    Orientation[] dirs = Orientation.values();
                    Orientation randomDir = dirs[rng.nextInt(dirs.length)];
                    plannedOrientation = randomDir;
                }

                // STRICT COMPLIANCE: Immediate detection if player is known
                if (lastKnowPlayerPosition != null) {
                    // Check probability or forced? "Si le joueur est vu, le passage en TARGETING
                    // doit être déterministe"
                    // We bypass stateCooldown check for reaction to detection
                    state = State.TARGETING;
                    targetingAnimation.reset();
                } else if (canChangeState && rng.nextDouble() < pTransition) {
                    // Only random transition to Targeting if we somehow got here without
                    // lastKnowPlayerPosition?
                    // Actually, lastKnowPlayerPosition is set by interaction.
                    // If we are here, it means no interaction yet, OR interaction just happened.
                    // If interaction happened, the IF above handles it.
                }
            }

            case TARGETING -> {
                if (lastKnowPlayerPosition == null) {
                    state = State.RANDOM;
                    randomAnimation.reset();
                    graphicPath = null;
                } else {

                    if (canReorient) {
                        Orientation target = computeTargetOrientation();
                        if (target != null) {
                            plannedOrientation = target;
                        }
                    }

                    // STRICT COMPLIANCE: No random abandonment in TARGETING state.
                    // Only transitions to RANDOM (if target lost) or SLEEPING (forced externally)
                    // are allowed.
                }
            }
        }

        switch (state) {
            // Pas de cas 'default' ici pour update, logique est ok
            case SLEEPING -> sleepingAnimation.update(deltaTime);
            case RANDOM -> randomAnimation.update(deltaTime);
            case TARGETING -> targetingAnimation.update(deltaTime);
        }

        // if (immune) {
        // blinkTick++;
        // if (immunityCd.ready(deltaTime)) {
        // immune = false;
        // }
        // }

        super.updateAlive(deltaTime);
    }

    @Override
    public Orientation getNextOrientation() {
        return plannedOrientation;
    }

    private Orientation computeTargetOrientation() {

        if (lastKnowPlayerPosition == null) {
            return null;
        }

        DiscreteCoordinates from = getCurrentMainCellCoordinates();

        ICMazeArea area = (ICMazeArea) getOwnerArea();

        Queue<Orientation> path = area.getShortestPath(from, lastKnowPlayerPosition);

        if (path == null || path.isEmpty()) {
            lastKnowPlayerPosition = null;
            graphicPath = null;
            return null;
        }

        graphicPath = new Path(this.getPosition(), new LinkedList<>(path));

        return path.poll();

    }

    @Override
    public boolean wantsViewInteraction() {
        // STRICT COMPLIANCE: Always wants view interaction to allow proximity wakeup
        return true;
    }

    @Override
    public boolean wantsCellInteraction() {
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
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {

            // CORRECTIF 1: "Faux-Sommeil"
            // Si le monstre dort ET que c'est une interaction de VUE -> IGNORER.
            // Il ne doit pas se réveiller juste parce que le joueur le regarde de loin.
            if (state == State.SLEEPING && !isCellInteraction) {
                return;
            }

            // CORRECTIF 2: "Invincibilité Post-Victoire"
            // Le signal de victoire empêche l'attaque et le réveil, MAIS ne doit pas
            // bloquer la méthode
            // si on voulait gérer autre chose (ex: sufferHit est géré ailleurs, mais ici on
            // gère l'attaque du monstre).
            // On déplace le return global pour cibler les actions offensives/réactives.
            boolean victory = (signal != null && signal.isOn());

            // LOGIQUE DE REVEIL (Seulement si pas victoire)
            if (!victory && state == State.SLEEPING) {
                // Ici c'est forcément une interaction de CELLULE (contact) car le cas VUE est
                // filtré au dessus
                state = State.RANDOM;
                randomAnimation.reset();
                stateCooldown.reset();
            }

            // LOGIQUE D'ATTAQUE / SUIVI (Seulement si pas victoire)
            if (!isCellInteraction) {

                DiscreteCoordinates playerPos = player.getCurrentMainCellCoordinates();
                DiscreteCoordinates front = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

                if (playerPos.equals(front)) {
                    // Attaque seulement si pas victoire
                    if (!victory) {
                        player.sufferHit();
                    }
                    // On met à jour la position connue (sauf si victoire -> on s'en fiche, il dort)
                    if (!victory) {
                        lastKnowPlayerPosition = playerPos;
                    }
                } else {
                    if (!victory) {
                        lastKnowPlayerPosition = playerPos;
                    }
                }
            }
        }
    }

    public void setLastKnowPlayerPosition(DiscreteCoordinates position) {
        this.lastKnowPlayerPosition = position;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * Monster takes damage
     */
    public void sufferHit() {

        if (immune || isDead()) {
            return;
        }

        // CORRECTIF: Réveil sur dégâts (si pas victoire)
        if (true) {
            boolean victory = (signal != null && signal.isOn());
            if (!victory) {
                state = State.RANDOM;
                randomAnimation.reset();
                stateCooldown.reset();
            }
        }

        loseHealth(1);
        healthBar.decrease(1);
        hasTakenDamage = true;
        if (isDead() && !killNotified) {
            ((ICMazeArea) getOwnerArea()).incrementMonsterKillCount();
            killNotified = true;
        }

        triggerImmunity();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }

    @Override
    public void draw(Canvas canvas) {

        if (isDead()) {
            super.draw(canvas);
            return;
        }

        boolean visible = !immune || (blinkTick % 2 == 0);

        if (state == State.SLEEPING) {
            sleepingAnimation.draw(canvas);
        } else if (visible) {
            switch (state) {
                case RANDOM -> randomAnimation.draw(canvas);
                case TARGETING -> targetingAnimation.draw(canvas);
                default -> sleepingAnimation.draw(canvas);
            }
        }

        // PROTECTION CHEMIN
        if (state == State.TARGETING && graphicPath != null) {
            graphicPath.draw(canvas);
        }

        if (!immune && hasTakenDamage && healthBar.isOn()) {
            healthBar.draw(canvas);
        }
    }
}
