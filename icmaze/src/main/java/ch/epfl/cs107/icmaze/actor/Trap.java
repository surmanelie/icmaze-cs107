package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Trap
 * An actor that oscillates between a safe and a dangerous state.
 * Can be deactivated by a Logic signal.
 */
public class Trap extends ICMazeActor implements Interactable, Logic {

    private final int damagePoint;
    private final Oscillator internalTimer;
    private final Logic deactivationSignal;

    private boolean isTrapActive;
    private final Sprite visual;

    private static final float DEFAULT_CYCLE = 2.0f;
    private static final int DEFAULT_DMG = 1;

    /**
     * Constructor for Trap with custom logic
     */
    public Trap(Area area, Orientation orientation, DiscreteCoordinates position, float period, int damage,
            Logic signal) {
        super(area, orientation, position);
        this.damagePoint = damage;
        this.internalTimer = new Oscillator(period);
        this.deactivationSignal = signal;
        this.isTrapActive = true;

        // Use "shadow" sprite with a specific depth to be under other actors but
        // visible
        this.visual = new Sprite("shadow", 1f, 1f, this);
        this.visual.setDepth(-10f); // Render below most things
    }

    /**
     * Default constructor using standard values
     */
    public Trap(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal) {
        this(area, orientation, position, DEFAULT_CYCLE, DEFAULT_DMG, signal);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // Check for deactivation
        if (deactivationSignal != null && deactivationSignal.isOn()) {
            isTrapActive = false;
        }

        // Only update oscillator if active
        if (isTrapActive) {
            internalTimer.update(dt);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Only draw if the trap is active AND in its "ON" phase
        if (isTrapActive && isOn()) {
            visual.draw(canvas);
        }
    }

    // --- Logic Implementation ---

    @Override
    public boolean isOn() {
        return isTrapActive && internalTimer.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public float getIntensity() {
        return (isTrapActive) ? internalTimer.getIntensity() : 0f;
    }

    // --- Interaction ---

    public int getDamage() {
        return damagePoint;
    }

    @Override
    public boolean takeCellSpace() {
        // Traps are walkable
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        // Can interact only if the trap is fundamentally active (not disabled by
        // signal)
        return isTrapActive;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }
}
