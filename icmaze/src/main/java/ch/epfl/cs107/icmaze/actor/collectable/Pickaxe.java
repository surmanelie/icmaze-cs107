package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Pickaxe extends Equipement{


    public Pickaxe(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation,position);
        setSprite( new Sprite("icmaze/pickaxe", 0.75f, 0.75f, this));

    }

    @Override
    public void acceptInteraction (AreaInteractionVisitor v, boolean isCellInteraction){
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }



//    @Override
//    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
//        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
//    }
}
