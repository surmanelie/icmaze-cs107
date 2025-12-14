package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.ICMazeObject;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.*;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.icmaze.actor.Portal;

import java.util.*;

import static ch.epfl.cs107.play.math.Orientation.*;

public class ICMazePlayer extends ICMazeActor implements Interactor {

    private final static int MOVE_DURATION = 4;

    private String name;
    private PlayerState currentState = PlayerState.IDLE;

    private final KeyBindings.PlayerKeyBindings keys;
    //private Keyboard keyboard = getOwnerArea().getKeyboard();

    private OrientedAnimation animation;

    private OrientedAnimation pickaxeAnimation;
    private static final int PICKAXE_ANIMATION_DURATION = 5;
// animation d’attaque à la pioche

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    private final List<ICMazeObject> bag = new ArrayList<>();
    private boolean isChanging;
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;

    //private static final int MAX_LIFE = 5;
    private final Health healthBar = new Health(this, Transform.I.translated(0,1.75f),5, true);

    //private int life = MAX_LIFE;

    private static final float IMMUNITY_DURATION = 1.0f;
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;

    //private float immunityTimer = 0f;



    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        this.name = spriteName;
        this.keys = KeyBindings.PLAYER_KEY_BINDINGS;

        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = {DOWN, RIGHT, UP, Orientation.LEFT};
        final int ANIMATION_DURATION = 4;
        final String prefix = "icmaze/player";

        animation = new OrientedAnimation(prefix, ANIMATION_DURATION, this, anchor, orders,
                4, 1, 2, 16, 32, true);


        // création de l'animation d'attaque
        final Vector anchor2 = new Vector(-.5f, 0);
        final Orientation[] orders2 = {DOWN , UP, RIGHT , LEFT};
        pickaxeAnimation= new  OrientedAnimation("icmaze/player.pickaxe",
                PICKAXE_ANIMATION_DURATION , this ,
                anchor2 , orders2 , 4, 2, 2, 32, 32);
        // // création de l'animation d'attaque
        //
        //        pickaxeAttackAnimation= new  OrientedAnimation("icmaze/player.pickaxe",
        //                PICKAXE_ANIMATION_DURATION , this ,
        //                anchor2 , orders2 , 4, 2, 2, 32, 32);
    }

    public enum PlayerState {
        IDLE,
        INTERACTING,
        ATTACKING_WITH_PICKAXE,
    }

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



    public void setDestinationArea(String destinationArea) {
        this.destinationArea = destinationArea;
    }

    @Override
    public void draw(Canvas canvas) {

        boolean visible = !immune || (blinkTick% 2 == 0);

        if(visible) {
            if(currentState == PlayerState.ATTACKING_WITH_PICKAXE){
                pickaxeAnimation.draw(canvas);
            }else{
                animation.draw(canvas);
            }
        }
        if(healthBar.isOn()){
            healthBar.draw(canvas);
        }
    }

    public DiscreteCoordinates getDestinationCoordonates() {
        return destinationCoordinates;
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



    @Override
    public boolean takeCellSpace() {
        return true;
    }

    private boolean hasHitThisAttack = false;

//    private boolean dead = false;
//    public boolean isDead(){
//        return dead;
//    }




    public void sufferHit(){

        if(immune) return;

        healthBar.decrease(1);

        immune = true;
        blinkTick = 0;
        immunityCd.reset();
//        immunityTimer = IMMUNITY_DURATION;

        if(healthBar.isOff()){
            //ça veut dire que le player est mort
            ((ICMazeArea) getOwnerArea()).requestReset();
        }

//        life--;
//        System.out.println("ICMAzePLayer a été touché par un LogMonster ! Vie = "+life);

//        if (life <= 0) {
//            System.out.println("ICMazePlayer est mort ");
//            dead = true;
//
////            ICMazeArea area = (ICMazeArea) getOwnerArea();
////            ICMaze game = (ICMaze) area.getOwner();
////
////            game.resetGame();
//
//        }
    }

//    private boolean isImmune(){
//        return immunityTimer > 0f;
//    }

    public void resetAfterAreaReset(){
        healthBar.resetHealth();

        immune = false;
        blinkTick = 0;
        immunityCd.reset();

        currentState = PlayerState.IDLE;
        animation.reset();
        pickaxeAnimation.reset();
        hasHitThisAttack = false;

        isChanging = false;
        destinationArea = null;
        destinationCoordinates = null;
    }

    @Override
    public void update(float deltaTime) {

        Keyboard keyboard = getOwnerArea().getKeyboard();

        switch (currentState) {

            case IDLE:
                moveIfPressed(DOWN, keyboard.get(keys.down()));
                moveIfPressed(RIGHT, keyboard.get(keys.right()));
                moveIfPressed(UP, keyboard.get(keys.up()));
                moveIfPressed(LEFT, keyboard.get(keys.left()));

                if (isDisplacementOccurs()) animation.update(deltaTime);
                else animation.reset();

                // Entrer en mode INTERACTING
                if (!isDisplacementOccurs() &&
                        keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerState.INTERACTING;
                }

                // Lancer animation d’attaque
                if (!isDisplacementOccurs() && hasPickaxe() && keyboard.get(keys.pickaxe()).isPressed()) {
                    currentState = PlayerState.ATTACKING_WITH_PICKAXE;
//                    animation = pickaxeAnimation;
                    pickaxeAnimation.reset();
                    hasHitThisAttack = false;
                }

                if (keyboard.get(keys.pickaxe()).isPressed()) {
                    System.out.println("PICKAXE pressed, hasPickaxe=" + hasPickaxe());
                }
                if (keyboard.get(keys.interact()).isPressed()) {
                    System.out.println("INTERACT pressed");
                }


                break;

            case INTERACTING:
                // Quitter mode INTERACTING
                if (!keyboard.get(keys.interact()).isDown()) {
                    currentState = PlayerState.IDLE;
                }
                break;

            case ATTACKING_WITH_PICKAXE:
                // --- On joue l’animation d’attaque ---
                pickaxeAnimation.update(deltaTime);
                System.out.println("entre en intercation ");

                // --- Quand l’animation finit, on revient à l’IDLE ---
                if (pickaxeAnimation.isCompleted()) {
                    System.out.println("redepaprt");
                    currentState = PlayerState.IDLE;

                    // remettre l'animation normale

                    animation.reset();
                    hasHitThisAttack = false; // prêt pour la prochaine attaque
                }

                break;
        }

        //immunityTimer = Math.max(0f, immunityTimer-deltaTime); // comme ça son temps d'immunité évolue à chaque update
        if (immune) {
            blinkTick++;
            if(immunityCd.ready(deltaTime)){ // ça vérifie si le temps d'immunité est terminé ou pass
                immune = false;
            }
        }
        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(MOVE_DURATION);
        }
    }

    //pour que le joueur puisse être vu par les monster

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() { return true; }

    @Override
    public boolean wantsViewInteraction() { return currentState == PlayerState.INTERACTING ||currentState == PlayerState.ATTACKING_WITH_PICKAXE; }

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
            if (isCellInteraction){
                heart.collect();
                healthBar.increase(1);
            }

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
                    destinationCoordinates = portal.getArrivalCoordinates();
                    //DiscreteCoordinates inside = arrival.jump(portal.getOrientation().opposite().toVector());
                }
            } else {

                //on doit d'abord vérifier qu'on ne fait rien si on attaque avec la pioche
                if (currentState != PlayerState.INTERACTING) {
                    return;
                }

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
        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {

            // Le joueur doit être en train d’attaquer AVEC la pioche
            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {

                rock.weaken(); // inflige 1 dégât au rocher
                hasHitThisAttack = true; // c'est ça qui permet de ne pas retaper pendant la même animation.

                // ATTENTION : le rock gère déjà vanish + drop + suppression
                // donc tu n’as rien d’autre à faire ici
            }

            System.out.println("Player -> Rock interaction, cell="+isCellInteraction+", state="+currentState);
        }

       @Override
       public void interactWith(LogMonster monster , boolean isCellInteraction) {

           if(!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {

               monster.sufferHit();
               hasHitThisAttack = true;
           }

           System.out.println("Player -> LogMonster interaction, cell="+isCellInteraction+", state="+currentState);
        }
    }



}

