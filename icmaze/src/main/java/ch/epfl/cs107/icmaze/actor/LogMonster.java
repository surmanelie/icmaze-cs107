package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique;
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
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, State initialState,
            Logic signal) {

        super(area, orientation, position, MAX_HEALTH, PERCEPTION_RADIUS);
        this.state = initialState;
        this.signal = signal;

        this.reorientCooldown = new Cooldown(0.75f);
        this.stateCooldown = new Cooldown(3.0f);

        int difficulty = Difficulty.MEDIUM; // default

        if (area instanceof AireLabyrinthique) {
            difficulty = ((AireLabyrinthique) area).getDifficulty();
        }

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
                ordersSleeping, 4, 2, 2, 32, 32, true);

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

        if (signal != null && signal.isOn()) {
            state = State.SLEEPING;
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
                }
            }

            case RANDOM -> {
                if (canReorient) {
                    Orientation[] dirs = Orientation.values();
                    Orientation randomDir = dirs[rng.nextInt(dirs.length)];
                    plannedOrientation = randomDir;
                }

                if (canChangeState && lastKnowPlayerPosition != null && rng.nextDouble() < pTransition) {
                    state = State.TARGETING;
                }
            }

            case TARGETING -> {
                if (lastKnowPlayerPosition == null) {
                    state = State.RANDOM;
                } else {

                    if (canReorient) {
                        Orientation target = computeTargetOrientation();
                        if (target != null) {
                            plannedOrientation = target;
                        }
                    }

                    if (canChangeState && rng.nextDouble() < (1.0 - pTransition)) {
                        state = State.SLEEPING;
                    }
                }
            }
        }

        switch (state) {
            case SLEEPING -> sleepingAnimation.update(deltaTime);
            case RANDOM -> randomAnimation.update(deltaTime);
            case TARGETING -> targetingAnimation.update(deltaTime);
        }

        if (immune) {
            blinkTick++;
            if (immunityCd.ready(deltaTime)) {
                immune = false;
            }
        }

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
        return state != State.SLEEPING;
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

            if (state == State.SLEEPING) {
                return;
            }

            if (!isCellInteraction) {

                DiscreteCoordinates playerPos = player.getCurrentMainCellCoordinates();
                DiscreteCoordinates front = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

                if (playerPos.equals(front)) {
                    player.sufferHit();
                } else {
                    lastKnowPlayerPosition = playerPos;
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

        loseHealth(1);
        healthBar.decrease(1);
        hasTakenDamage = true;
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

        if (visible) {
            switch (state) {
                case SLEEPING -> sleepingAnimation.draw(canvas);
                case RANDOM -> randomAnimation.draw(canvas);
                case TARGETING -> targetingAnimation.draw(canvas);
            }
        }

        if (graphicPath != null) {
            graphicPath.draw(canvas);
        }

        if (!immune && hasTakenDamage && healthBar.isOn()) {
            healthBar.draw(canvas);
        }
    }
}
