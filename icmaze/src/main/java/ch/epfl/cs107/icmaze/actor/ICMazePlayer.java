package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.ICMazeObject;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.collectable.Coin;
import ch.epfl.cs107.icmaze.actor.collectable.SpeedBall;
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

    private final static int NORMAL_MOVE_DURATION = 4;
    private final static int FAST_MOVE_DURATION = 2;
    private final static int SLOW_MOVE_DURATION = 8;

    private int currentMoveDuration = NORMAL_MOVE_DURATION;
    private Cooldown speedCd = new Cooldown(0f);
    private boolean speedEffectActive = false;

    private boolean speedVisualActive = false;
    private int speedBlinkTick = 0;
    private static final int SPEED_BLINK_RATE = 2; // très rapide

    private String name;
    private PlayerState currentState = PlayerState.IDLE;

    private final KeyBindings.PlayerKeyBindings keys;

    private OrientedAnimation animation;

    private OrientedAnimation pickaxeAnimation;
    private static final int PICKAXE_ANIMATION_DURATION = 5;

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    private final List<ICMazeObject> bag = new ArrayList<>();
    private boolean isChanging;
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;

    private final Health healthBar = new Health(this, Transform.I.translated(0, 1.75f), 5, true);

    private static final float IMMUNITY_DURATION = 1.0f;
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;

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

        final Vector anchor2 = new Vector(-.5f, 0);
        final Orientation[] orders2 = { DOWN, UP, RIGHT, LEFT };
        pickaxeAnimation = new OrientedAnimation("icmaze/player.pickaxe",
                PICKAXE_ANIMATION_DURATION, this,
                anchor2, orders2, 4, 2, 2, 32, 32);

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

    public int getCoinCount() {
        int count = 0;
        for (ICMazeObject obj : bag) {
            if (obj instanceof Coin) {
                count++;
            }
        }
        return count;
    }

    public void setDestinationArea(String destinationArea) {
        this.destinationArea = destinationArea;
    }

    @Override
    public void draw(Canvas canvas) {

        boolean visible = true;

        if (immune) {
            visible = (blinkTick % 2 == 0);
        }

        if (speedVisualActive) {
            visible = (speedBlinkTick % SPEED_BLINK_RATE == 0);
        }

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

    public void sufferHit() {

        if (immune)
            return;

        healthBar.decrease(1);

        triggerVisualEffect();

        if (!healthBar.isOn()) {

            ((ICMazeArea) getOwnerArea()).requestReset();
        }
    }

    public void triggerVisualEffect() {
        immune = true;
        blinkTick = 0;
        immunityCd.reset();
    }

    public void resetAfterAreaReset() {
        healthBar.resetHealth();

        bag.removeIf(item -> item instanceof Key);

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

        if (!healthBar.isOn()) {
            return;
        }

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

                if (!isDisplacementOccurs() &&
                        keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerState.INTERACTING;
                }

                if (!isDisplacementOccurs() && hasPickaxe() && keyboard.get(keys.pickaxe()).isPressed()) {
                    currentState = PlayerState.ATTACKING_WITH_PICKAXE;
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
                if (!keyboard.get(keys.interact()).isDown()) {
                    currentState = PlayerState.IDLE;
                }
                break;

            case ATTACKING_WITH_PICKAXE:
                pickaxeAnimation.update(deltaTime);

                if (pickaxeAnimation.isCompleted()) {

                    currentState = PlayerState.IDLE;

                    animation.reset();
                    hasHitThisAttack = false;
                }

                break;
        }

        if (immune) {
            blinkTick++;
            if (immunityCd.ready(deltaTime)) { // ça vérifie si le temps d'immunité est terminé ou pass
                immune = false;
            }
        }

        if (speedEffectActive) {
            speedBlinkTick++;
            if (speedCd.ready(deltaTime)) {
                speedEffectActive = false;
                currentMoveDuration = NORMAL_MOVE_DURATION;
                speedVisualActive = false;
            }
        }
        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(currentMoveDuration);
        }
    }

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

            }
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) {
                heart.collect();
                healthBar.increase(1);
                triggerVisualEffect();
            }

        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(key);
                key.collect();

            }
        }

        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction) {
                // Interaction de cellule : on marche sur un portail OUVERT -> téléportation
                if (portal.getState() == Portal.State.OPEN) {
                    setisChanging(true);
                    setDestinationArea(portal.getDestinationAreaName());
                    destinationCoordinates = portal.getArrivalCoordinates();

                }
            } else {

                if (currentState != PlayerState.INTERACTING) {
                    return;
                }

                if (portal.getState() == Portal.State.LOCKED) {
                    int id = portal.getKeyId();

                    if (id == Portal.NO_KEY_ID) {
                        return;
                    }

                    if (hasKey(id) && useKey(id)) {
                        portal.open();

                    }
                }
            }
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {

            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {

                rock.weaken();
                hasHitThisAttack = true;

            }

        }

        @Override
        public void interactWith(LogMonster monster, boolean isCellInteraction) {

            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {

                monster.sufferHit();
                hasHitThisAttack = true;
            }

        }

        @Override
        public void interactWith(Boss boss, boolean isCellInteraction) {
            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {
                boss.sufferHit();
                hasHitThisAttack = true;
            }
        }

        @Override
        public void interactWith(FinalLieutenant lieutenant, boolean isCellInteraction) {
            if (!isCellInteraction && currentState == PlayerState.ATTACKING_WITH_PICKAXE && !hasHitThisAttack) {
                lieutenant.loseHealth(1);
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

        @Override
        public void interactWith(SpeedBall ball, boolean isCellInteraction) {
            if (isCellInteraction) {
                bag.add(ball);
                ball.collect();

                switch (ball.effect()) {
                    case FAST -> currentMoveDuration = FAST_MOVE_DURATION;
                    case SLOW -> currentMoveDuration = SLOW_MOVE_DURATION;
                }

                speedCd = new Cooldown(ball.durationSeconds());
                speedCd.reset();
                speedEffectActive = true;

                speedVisualActive = true;
                speedBlinkTick = 0;
            }
        }

        @Override
        public void interactWith(Trap trap, boolean isCellInteraction) {
            if (isCellInteraction && trap.isOn()) {
                sufferHit();
            }
        }
    }
}
