package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Rock  extends AreaEntity {

    private Sprite sprite;

    public Rock(Area area, DiscreteCoordinates coordinates){
        super(area, Orientation.DOWN,coordinates);
        sprite = new Sprite(
                "rock.2",
                1f,
                1f,
                this);

    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public boolean takeCellSpace() {
        return true; // NON traversable
    }

    @Override
    public boolean isCellInteractable() {
        return true; // Accepte interactions de contact
    }

    @Override
    public boolean isViewInteractable() {
        return true; // Accepte interactions à distance
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Pour le moment : ne fait rien, comportement par défaut
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }
}

