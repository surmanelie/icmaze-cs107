package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;


public abstract class ICMazeObject extends CollectableAreaEntity {

    private boolean isCollected;


    public ICMazeObject (Area area, Orientation orientation, DiscreteCoordinates position){
        super(area,orientation,position);
        isCollected = false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }


    @Override
    public boolean takeCellSpace() {
        return false;
    }



    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable(){
        return false;
    }

    public void collect() {
        if (!isCollected) {
            isCollected = true;
            getOwnerArea().unregisterActor(this);
        }

    }


}
