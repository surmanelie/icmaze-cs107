package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.signal.logic.Logic;

/**
 * Oscillator
 * Represents a logic signal that oscillates between true (ON) and false (OFF)
 * states
 * over a defined time period.
 */
public class Oscillator implements Logic {

    private final float cycleDuration;
    private float currentTime = 0f;

    public Oscillator(float period) {
        this.cycleDuration = period;
    }

    /**
     * Updates the internal timer of the oscillator.
     * 
     * @param dt Delta time since last update
     */
    public void update(float dt) {
        currentTime += dt;
        if (currentTime >= cycleDuration) {
            currentTime -= cycleDuration;
        }
    }

    @Override
    public boolean isOn() {
        // Active for the first half of the cycle
        return currentTime < (cycleDuration / 2.0f);
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public float getIntensity() {
        if (isOn()) {
            return 1.0f;
        }
        return 0.0f;
    }
}
