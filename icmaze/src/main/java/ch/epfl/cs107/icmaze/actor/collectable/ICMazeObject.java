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




    public ICMazeObject (Area area, Orientation orientation, DiscreteCoordinates position){
        super(area,orientation,position);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of();
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

    @Override
    public void collect() {
        super.collect();
        getOwnerArea().unregisterActor(this);
    }
}
