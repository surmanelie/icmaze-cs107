package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class BlueSpeedBall extends SpeedBall {

    public BlueSpeedBall(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "icmaze/staff_water.icon");
    }

    @Override
    public Effect effect() {
        return Effect.FAST;
    }

    @Override
    public float durationSeconds() {
        return 10f;
    }
}
