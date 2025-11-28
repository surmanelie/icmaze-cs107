package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.icmaze.actor.ICMazePlayer.PlayerState.IDLE;
import static ch.epfl.cs107.icmaze.actor.ICMazePlayer.PlayerState.INTERACTING;

public class ICMazePlayer extends ICMazeActor implements Interactor {

    private final static int  MOVE_DURATION = 4;
    private String name;
    private PlayerState currentState = IDLE;
    private KeyBindings.PlayerKeyBindings key;
    private Keyboard keyboard = getOwnerArea().getKeyboard(); //est-ce qu'il faut mettre en private ?
    private OrientedAnimation animation;
    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();



    public ICMazePlayer (Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName,KeyBindings.PlayerKeyBindings key ){
        super(owner, orientation, coordinates);
        this.name = spriteName;
        this.key = key;
        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = { Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT }; //on peut faire ça comme ça ?
        final int ANIMATION_DURATION = 4;
        final String prefix = "icmaze/player";
        animation = new OrientedAnimation(prefix, ANIMATION_DURATION, this, anchor, orders, 4, 1, 2, 16, 32, true);
    }

    public enum PlayerState{
        IDLE,
        INTERACTING
    }

    @Override //on dit que ce joueur n'est pas traversable, i.e il prend la place de la cellule
    public boolean takeCellSpace(){
        return true;
    }

    @Override
    public void update(float deltaTime) { //vérifier que l'update est bon
        switch (currentState){
            case IDLE :
                moveIfPressed(Orientation.DOWN, keyboard.get(key.down()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(key.right()));
                moveIfPressed(Orientation.UP, keyboard.get(key.up()));
                moveIfPressed(Orientation.LEFT, keyboard.get(key.left()));
                if(isDisplacementOccurs()) {
                    animation.update(deltaTime);
                }else{
                    animation.reset();
                }
                break;

            case INTERACTING:
                break;
        }

        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button b){//vérifier que c'est bien cette fonction qu'il fallait faire
        if(b.isDown()){
            if(!isDisplacementOccurs()){
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    @Override
    public void draw (Canvas canvas){
        animation.draw(canvas);
    }





    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells(){
         return Collections.singletonList (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction(){
        return true;
    }

    @Override
    public boolean wantsViewInteraction(){
    if (currentState == INTERACTING){
        return true;

    }else
        return false;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);

    }
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {
        wantsCellIntercation(){

        }

    }




}
