package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Key extends ICMazeObject{

    private final int  id;
    public final Sprite sprite;

    public Key(Area area, Orientation orientation, DiscreteCoordinates position, int id){
        super(area,orientation,position);
        this.id = id;
        this.sprite = new Sprite("icmaze/key",1f,1f,this);
    }

    public int getId(){
        return id;
    }

//    @Override
//    public void collect() {
//        getOwnerArea().unregisterActor(this);
//    }
    //c'est deja défini dans ICMazeObject, donc pas besoin de refaire ici

    @Override
    public void draw(Canvas canvas){
        sprite.draw(canvas);
    }

//    @Override
//    public List<DiscreteCoordinates> getCurrentCells() {
//        return Collections.singletonList(getCurrentMainCellCoordinates());
//    }
    //lui aussi ça sert à rien. il est un peu différent de ICMazeObject, mais en vrai ça fait exactement la même chose donc en s'en fout

//    @Override
//    public boolean takeCellSpace() {
//        return false;
//
//    }
    //ne sert à rien aussi car aucun changement par rapport à la super classe

//    @Override
//    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
//        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
//
//    }

//    @Override
//    public boolean isViewInteractable() {
//        return false;
//    }
    //elle ne sert à rien car aucun changement avec la super classe

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if(v instanceof  ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }
}
