package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

public abstract class ICMazeActor extends MovableAreaEntity implements Interactable {

    public ICMazeActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        // spriteName = new Sprite(sprite, 1.f, 1.f, this); est-ce qu'il faut mettre un
        // sprite ici ?
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    // est-ce qu'on pourrait mettre update ici ou pas ?

    public void leaveArea() {
        System.out.println("leaveArea from " + getOwnerArea().getTitle() + " at " + getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        System.out.println("enterArea in " + area.getTitle() + " at " + position);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
        area.registerActor(this);
    }

}
