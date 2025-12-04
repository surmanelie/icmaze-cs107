package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.ICMazeObject;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.*;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.icmaze.actor.Portal;

import java.util.*;

public class ICMazePlayer extends ICMazeActor implements Interactor {

    private final static int MOVE_DURATION = 4;

    private String name;
    private PlayerState currentState = PlayerState.IDLE;

    private final KeyBindings.PlayerKeyBindings keys;
    private Keyboard keyboard = getOwnerArea().getKeyboard();

    private OrientedAnimation animation;
    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    private final List<ICMazeObject> bag = new ArrayList<>();
    private boolean isChanging;
    private String destinationArea;
    private DiscreteCoordinates destinationCoordonates;

    public boolean hasKey(int id){
        for(ICMazeObject object : bag){
            if(object instanceof Key key && key.getId() == id){
                return true;
            }
        }
        return false;
    }

    public boolean useKey(int id){
        for (int i = 0; i < bag.size(); i++) {
            ICMazeObject object  = bag.get(i);
            if (object instanceof Key key && key.getId() == id) {
                bag.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean hasPickaxe(){
        for (ICMazeObject object : bag) {
            if (object instanceof  Pickaxe) {
                return true;
            }
        }
        return false;
    }

    //boolenan changing set + get
    //string destinationArea get
    //corrdonne arrive get


    public void setDestinationArea(String destinationArea) {
        this.destinationArea = destinationArea;
    }

    public DiscreteCoordinates getDestinationCoordonates() {
        return destinationCoordonates;
    }

    public void setisChanging(boolean changing) {
        isChanging = changing;
    }

    public boolean getisChanging() {
        return isChanging;
    }



    public String getDestinationArea() {
        return destinationArea;
    }

    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        this.name = spriteName;
        this.keys = KeyBindings.PLAYER_KEY_BINDINGS;

        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
        final int ANIMATION_DURATION = 4;
        final String prefix = "icmaze/player";

        animation = new OrientedAnimation(prefix, ANIMATION_DURATION, this, anchor, orders,
                4, 1, 2, 16, 32, true);
    }

    public enum PlayerState {
        IDLE,
        INTERACTING
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public void update(float deltaTime) {

        switch (currentState) {

            case IDLE:
                moveIfPressed(Orientation.DOWN, keyboard.get(keys.down()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(keys.right()));
                moveIfPressed(Orientation.UP, keyboard.get(keys.up()));
                moveIfPressed(Orientation.LEFT, keyboard.get(keys.left()));

                if (isDisplacementOccurs()) animation.update(deltaTime);
                else animation.reset();

                // Entrer en mode INTERACTING
                if (!isDisplacementOccurs() &&
                        keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerState.INTERACTING;
                }
                break;

            case INTERACTING:
                // Quitter mode INTERACTING
                if (!keyboard.get(keys.interact()).isDown()) {
                    currentState = PlayerState.IDLE;
                }
                break;
        }

        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(MOVE_DURATION);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() { return true; }

    @Override
    public boolean wantsViewInteraction() { return currentState == PlayerState.INTERACTING; }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(pickaxe);
                pickaxe.collect();
                //getOwnerArea().unregisterActor(pickaxe); //à vérifier si c'est vraiment nécessaire
                // j'ai vérifié et ça change rien si on appelle pas unregistor car le collect est bon mntn
                //System.out.println("sdfghj");
                //mntn il faut faire effacer l'objet de la map
                //pickaxe.unregister(pickaxe);
            }
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) heart.collect();
            //getOwnerArea().unregisterActor(heart);
            //System.out.println("sdfghj");
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(key);
                key.collect();
                //System.out.println("sdfghj");
            }
        }

//        @Override
//        public void interactWith(Portal portal, boolean isCellInteraction) {
//            setisChanging(true);
//            setDestinationArea(portal.getDestinationAreaName());
//            destinationCoordonates = portal.getArrivalCoordinates();
//        }
        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction) {
                // Interaction de cellule : on marche sur un portail OUVERT -> téléportation
                if (portal.getState() == Portal.State.OPEN) {
                    setisChanging(true);
                    setDestinationArea(portal.getDestinationAreaName());
                    destinationCoordonates = portal.getArrivalCoordinates();
                    //DiscreteCoordinates inside = arrival.jump(portal.getOrientation().opposite().toVector());
                }
            } else {
                // Interaction de vue : on est en mode INTERACTING et on regarde le portail
                if (portal.getState() == Portal.State.LOCKED) {
                    int id = portal.getKeyId();

                    // Si le portail n'utilise pas de clé (id spécial), on ne fait rien
                    if (id == Portal.NO_KEY_ID) {
                        return;
                    }

                    // Si on a la bonne clé, on la consomme et on ouvre le portail
                    if (hasKey(id) && useKey(id)) {
                        portal.open();
                        // On choisit de ne PAS téléporter tout de suite :
                        // le joueur devra ensuite passer dessus pour se téléporter.
                    }
                }
                // Si le portail est INVISIBLE ou déjà OPEN : rien à faire en view interaction.
            }
        }

    }



}

