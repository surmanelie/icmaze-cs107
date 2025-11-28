package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Heart extends ICMazeObject{
    private Animation animation;
    private final static int ANIMATION_DURATION = 24;


    public Heart (Area area, Orientation orientation){//est ce qu'il faut faire un autre spriteName et une autre position qui correspond au coeur ?
        super(area, orientation);
        animation  = new Animation("icmaze/heart", 4,1,1,this,16,16, ANIMATION_DURATION/4, true);
    }
    @Override
    public abstract void draw (Canvas canvas);
}
