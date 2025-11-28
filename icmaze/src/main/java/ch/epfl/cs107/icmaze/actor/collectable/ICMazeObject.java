package ch.epfl.cs107.icmaze.actor.collectable;
import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.geom.Area;

public abstract class ICMazeObject extends ICMazeActor {
    private final Sprite sprite;


    public ICMazeObject (Area area, Orientation orientation, DiscreteCoordinates position, String spriteName){
        super(area,orientation,position);
        sprite = new Sprite(spriteName,1,1,this);
    }


    public boolean isCellIntercatable(){
        return true;
    }
    @Override
    public void draw(Canvas canvas){
        sprite.draw(canvas);
    }
}
