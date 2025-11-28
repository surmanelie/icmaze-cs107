package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Equipement extends ICMazeObject{

    private Sprite sprite;

    public Equipement(Area area , Orientation orientation, DiscreteCoordinates position){
        super(area,orientation,position);

    }

    @Override
    public void draw(Canvas canvas){
        sprite.draw(canvas);
    }

    public void setSprite(Sprite sprite){
        this.sprite  = sprite ;
    }


    //penser à override ici la méthode draw

}
