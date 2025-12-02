package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public  class Heart extends ICMazeObject{
    private Animation animation;
    private final static int ANIMATION_DURATION = 24;


    public Heart (Area area, DiscreteCoordinates position){//est ce qu'il faut faire un autre spriteName et une autre position qui correspond au coeur ?
        super(area, Orientation.DOWN, position);

        animation  = new Animation("icmaze/heart", 4,1,1,this,16,16, ANIMATION_DURATION/4, true);
    }
    @Override
    public  void draw (Canvas canvas){
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
    }

//    @Override
//    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
//        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
//    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this,isCellInteraction);
        }
    }

}
