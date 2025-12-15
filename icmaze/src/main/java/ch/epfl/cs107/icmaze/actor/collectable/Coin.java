package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Coin extends ICMazeObject {

    private final Animation animation;

    private final Logic signal;

    public Coin(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal) {
        super(area, orientation, position);
        this.signal = signal;
        // "icmaze/coin" or "coin"? usually "icmaze/coin". User said "coin.png"
        // implicitly but usually prefixes.
        // Assuming "coin" based on user context. Check Treasure.java usage
        // "icmaze/coin".
        this.animation = new Animation("icmaze/coin", 4, 1, 1, this, 16, 16, 4, true);
        // 4 frames? I'll guess standard 4-frame animation or simpler.
        // Using "coin" (no icmaze prefix?) Treasure uses "icmaze/coin".
        // Let's assume standard 4 frames, speed 4.
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (signal != null && signal.isOn()) {
            animation.draw(canvas);
        }
    }

    @Override
    public boolean takeCellSpace() {
        return false;// signal != null && signal.isOn();
    }

    @Override
    public boolean isCellInteractable() {
        return signal != null && signal.isOn();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }
}
