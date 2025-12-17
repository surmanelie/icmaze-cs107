package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.ICMazeObject;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.collectable.Coin;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;
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
    // private Keyboard keyboard = getOwnerArea().getKeyboard();

    private OrientedAnimation animation;

    private OrientedAnimation pickaxeAnimation;
    private static final int PICKAXE_ANIMATION_DURATION = 5;
    // animation d’attaque à la pioche

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    private final List<ICMazeObject> bag = new ArrayList<>();
    private boolean isChanging;
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;

    // private static final int MAX_LIFE = 5;
    private final Health healthBar = new Health(this, Transform.I.translated(0, 1.75f), 5, true);

    // private int life = MAX_LIFE;

    private static final float IMMUNITY_DURATION = 1.0f;
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;

    // private float immunityTimer = 0f;

    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        this.name = spriteName;
        this.keys = KeyBindings.PLAYER_KEY_BINDINGS;

        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = { DOWN, RIGHT, UP, Orientation.LEFT };
        final int ANIMATION_DURATION = 4;
        final String prefix = "icmaze/player";

        animation = new OrientedAnimation(prefix, ANIMATION_DURATION, this, anchor, orders,
                4, 1, 2, 16, 32, true);

        // création de l'animation d'attaque
        final Vector anchor2 = new Vector(-.5f, 0);
        final Orientation[] orders2 = { DOWN, UP, RIGHT, LEFT };
        pickaxeAnimation = new OrientedAnimation("icmaze/player.pickaxe",
                PICKAXE_ANIMATION_DURATION, this,
                anchor2, orders2, 4, 2, 2, 32, 32);
        // // création de l'animation d'attaque
        //
        // pickaxeAttackAnimation= new OrientedAnimation("icmaze/player.pickaxe",
        // PICKAXE_ANIMATION_DURATION , this ,
        // anchor2 , orders2 , 4, 2, 2, 32, 32);
    }

    public enum PlayerState {
        IDLE,
        INTERACTING,
        ATTACKING_WITH_PICKAXE,
    }

    public boolean hasKey(int id) {
        for (ICMazeObject object : bag) {
            if (object instanceof Key key && key.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean useKey(int id) {
        for (int i = 0; i < bag.size(); i++) {
            ICMazeObject object = bag.get(i);
            if (object instanceof Key key && key.getId() == id) {
                bag.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean hasPickaxe() {
        for (ICMazeObject object : bag) {
            if (object instanceof Pickaxe) {
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

        boolean visible = !immune || (blinkTick % 2 == 0);

        if (visible) {
            if (currentState == PlayerState.ATTACKING_WITH_PICKAXE) {
                pickaxeAnimation.draw(canvas);
            } else {
                animation.draw(canvas);
            }
        }
        if (healthBar.isOn()) {
            healthBar.draw(canvas);
        }
        drawHUD(canvas);
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

    // private boolean dead = false;
    // public boolean isDead(){
    // return dead;
    // }

    public void sufferHit() {

        if (immune)
            return;

        healthBar.decrease(1);

        triggerVisualEffect();

        if (!healthBar.isOn()) {
            // ça veut dire que le player est mort
            ((ICMazeArea) getOwnerArea()).requestReset();
        }
    }

    public void triggerVisualEffect() {
        immune = true;
        blinkTick = 0;
        immunityCd.reset();
    }

    // private boolean isImmune(){
    // return immunityTimer > 0f;
    // }

    public void resetAfterAreaReset() {
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

                if (isDisplacementOccurs())
                    animation.update(deltaTime);
                else
                    animation.reset();

                // Entrer en mode INTERACTING
                if (!isDisplacementOccurs() &&
                        keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerState.INTERACTING;
                }

                // Lancer animation d’attaque
                if (!isDisplacementOccurs() && hasPickaxe() && keyboard.get(keys.pickaxe()).isPressed()) {
                    currentState = PlayerState.ATTACKING_WITH_PICKAXE;
                    // animation = pickaxeAnimation;
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

                // --- Quand l’animation finit, on revient à l’IDLE ---
                if (pickaxeAnimation.isCompleted()) {

                    currentState = PlayerState.IDLE;

                    // remettre l'animation normale

                    animation.reset();
                    hasHitThisAttack = false; // prêt pour la prochaine attaque
                }

                break;
        }

        // immunityTimer = Math.max(0f, immunityTimer-deltaTime); // comme ça son temps
        // d'immunité évolue à chaque update
        if (immune) {
            blinkTick++;
            if (immunityCd.ready(deltaTime)) { // ça vérifie si le temps d'immunité est terminé ou pass
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

    // pour que le joueur puisse être vu par les monster

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
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return currentState == PlayerState.INTERACTING || currentState == PlayerState.ATTACKING_WITH_PICKAXE;
    }

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
                // getOwnerArea().unregisterActor(pickaxe); //à vérifier si c'est vraiment
                // nécessaire
                // j'ai vérifié et ça change rien si on appelle pas unregistor car le collect
                // est bon mntn

                // mntn il faut faire effacer l'objet de la map
                // pickaxe.unregister(pickaxe);
            }
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) {
                heart.collect();
                healthBar.increase(1);
                triggerVisualEffect();
            }

            // getOwnerArea().unregisterActor(heart);

        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(key);
                key.collect();

            }
        }

        // @Override
        // public void interactWith(Portal portal, boolean isCellInteraction) {
        // setisChanging(true);
        // setDestinationArea(portal.getDestinationAreaName());
        // destinationCoordonates = portal.getArrivalCoordinates();
        // }
        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction) {
                // Interaction de cellule : on marche sur un portail OUVERT -> téléportation
                if (portal.getState() == Portal.State.OPEN) {
                    setisChanging(true);
                    setDestinationArea(portal.getDestinationAreaName());
                    destinationCoordinates = portal.getArrivalCoordinates();
                    // DiscreteCoordinates inside =
                    // arrival.jump(portal.getOrientation().opposite().toVector());
                }
            } else {

                // on doit d'abord vérifier qu'on ne fait rien si on attaque avec la pioche
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

        }

        @Override
        public void interactWith(LogMonster monster, boolean isCellInteraction) {

            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {

                monster.sufferHit();
                hasHitThisAttack = true;
            }

            System.out
                    .println("Player -> LogMonster interaction, cell=" + isCellInteraction + ", state=" + currentState);
        }

        @Override
        public void interactWith(Boss boss, boolean isCellInteraction) {
            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {
                boss.sufferHit();
                hasHitThisAttack = true;
            }
        }

        @Override
        public void interactWith(Coin coin, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(coin);
                coin.collect();
            }
        }
    }

    // private void drawHUD(Canvas canvas) {
    // int coinCount = 0;
    // for (ICMazeObject obj : bag) {
    // if (obj instanceof Coin)
    // coinCount++;
    // }
    //
    // // HUD Fixed Position (Screen Space)
    // // Canvas.getPosition() returns the camera center
    // float width = (float) canvas.getScaledWidth();
    // float height = (float) canvas.getScaledHeight();
    // Vector viewCenter = canvas.getPosition();
    // Vector topLeft = viewCenter.add(new Vector(-width / 2, height / 2));
    //
    // // HUD size is 4x2 units. Anchor is center of image.
    // // We place it at Top Left + margin.
    // Vector anchor = topLeft.add(new Vector(2f + 0.5f, -1f - 0.5f));
    //
    // // 1. Coin Display (64x32 px -> 4x2 units)
    // ImageGraphics coinIcon = new ImageGraphics(
    // ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/coinsDisplay"),
    // 4f, 2f, new RegionOfInterest(0, 0, 64, 32),
    // anchor, 1f, 2000f);
    // coinIcon.draw(canvas);
    //
    // // 2. Digits
    // // Display in the right half of the 4x2 area.
    // // Right half center relative to anchor: (+1, 0).
    // // Digits size 0.5x0.5 ?
    // // 3 digits max. Total width 1.5. Fits in 2.0.
    //
    // String countStr = String.valueOf(coinCount);
    // float digitSize = 0.5f;
    //
    // // Start drawing digits centered in the right half
    // // Right half x range: [0, 2] relative to HUD center? No, HUD is [-2, 2].
    // // Right half is [0, 2].
    // // Center of right half is x=1.
    //
    // // Let's center the string of digits around x=1 relative to anchor.
    // float totalWidth = countStr.length() * digitSize;
    // float startX = 1f - (totalWidth / 2) + (digitSize / 2);
    // // Logic: if 1 digit (width 0.5), center at 1. Start at 1.
    // // Wait, anchor is center of digit? ImageGraphics anchor is center.
    // // So we place digit centers.
    //
    // // Let's simplify: Start at x = 0.5 (left of right half) + margin
    // // Right half starts at anchor.x (since anchor is center of 4-wide image).
    // // Correct.
    //
    // for (int i = 0; i < countStr.length(); i++) {
    // int digit = Character.getNumericValue(countStr.charAt(i));
    //
    // int regionX = 0;
    // int regionY = 0;
    //
    // if (digit == 0) {
    // regionX = 16;
    // regionY = 32;
    // } else {
    // int n = digit - 1;
    // int col = n % 4;
    // int row = n / 4;
    // regionX = col * 16;
    // regionY = row * 16;
    // }
    //
    // ImageGraphics digitGraphics = new ImageGraphics(
    // ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/digits"),
    // digitSize, digitSize, new RegionOfInterest(regionX, regionY, 16, 16),
    // anchor.add(new Vector(0.4f + i * 0.6f, 0)), // Manual offset into right half
    // 1f, 2001f);
    // digitGraphics.draw(canvas);
    // }
    // }

    // Dans ICMazePlayer.java

    private void drawHUD(Canvas canvas) {
        int coinCount = 0;
        for (ICMazeObject obj : bag) {
            if (obj instanceof Coin)
                coinCount++;
        }

        // --- CORRECTION DU SCALE FACTOR ---
        // Ajout du cast (float) car canvas.getScaledHeight() retourne un double
        float scaleRatio = (float) canvas.getScaledHeight() / 11.0f;

        // Dimensions de base (adaptées par le ratio)
        float hudWidth = 4f * scaleRatio;
        float hudHeight = 2f * scaleRatio;
        float digitSize = 0.5f * scaleRatio;

        // Calcul de la position (Coin Haut-Gauche)
        Vector viewCenter = canvas.getPosition();

        // Ajout des casts (float) ici aussi
        float viewWidth = (float) canvas.getScaledWidth();
        float viewHeight = (float) canvas.getScaledHeight();

        Vector topLeft = viewCenter.add(new Vector(-viewWidth / 2, viewHeight / 2));

        // On place l'ancre en haut à gauche avec une petite marge proportionnelle
        Vector anchor = topLeft
                .add(new Vector((hudWidth / 2) + (0.5f * scaleRatio), -(hudHeight / 2) - (0.5f * scaleRatio)));

        // 1. Dessin du fond (Coins Display)
        ImageGraphics coinIcon = new ImageGraphics(
                ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/coinsDisplay"),
                hudWidth, hudHeight,
                new RegionOfInterest(0, 0, 64, 32),
                anchor, 1f, 2000f);
        coinIcon.draw(canvas);

        // 2. Dessin des chiffres (Centrés dans la moitié droite du HUD)
        String countStr = String.valueOf(coinCount);

        // On calcule le décalage pour centrer les chiffres
        float numberStartX = (hudWidth / 4) - ((countStr.length() * digitSize) / 2);

        for (int i = 0; i < countStr.length(); i++) {
            int digit = Character.getNumericValue(countStr.charAt(i));
            int regionX = 0;
            int regionY = 0;

            if (digit == 0) {
                regionX = 16;
                regionY = 32;
            } else {
                int n = digit - 1;
                int col = n % 4;
                int row = n / 4;
                regionX = col * 16;
                regionY = row * 16;
            }

            ImageGraphics digitGraphics = new ImageGraphics(
                    ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/digits"),
                    digitSize, digitSize,
                    new RegionOfInterest(regionX, regionY, 16, 16),
                    anchor.add(new Vector(numberStartX + (i * digitSize), 0)),
                    1f, 2001f);
            digitGraphics.draw(canvas);
        }
    }

}
