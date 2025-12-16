package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Pickaxe
 * Class representing a collectable Pickaxe in the ICMaze
 */
public class Pickaxe extends Equipement {

    /**
     * Default Pickaxe constructor
     * 
     * @param area        (Area): Owner Area
     * @param orientation (Orientation): Initial orientation
     * @param position    (DiscreteCoordinates): Initial position
     */
    public Pickaxe(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        setSprite(new Sprite("icmaze/pickaxe", 0.75f, 0.75f, this));
    }

    /**
     * Accept interaction from a visitor
     * 
     * @param v                 (AreaInteractionVisitor): The visitor
     * @param isCellInteraction (boolean): True if interaction is cell-based
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
