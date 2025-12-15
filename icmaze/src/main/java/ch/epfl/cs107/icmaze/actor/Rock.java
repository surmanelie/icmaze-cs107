package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.Updatable;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Rock extends AreaEntity implements Interactable, Updatable {

    // Sprite principal du rocher
    private Sprite sprite;

    // Points de vie restants (le rocher disparaît à 0)
    // private int hP = 3;

    // private boolean recentlyHit = false;

    // Indique si le rocher est en train de disparaître
    private boolean vanishing = false;

    // Animation de disparition
    private static final int ANIMATION_DURATION = 24;
    private final Animation vanishAnimation;

    private final Health healthBar = new Health(this, Transform.I.translated(0, 1.0f), 3, false);

    // Initialise le rocher, son sprite et l’animation de disparition

    public Rock(Area area, DiscreteCoordinates coordinates) {// ,int hitPoints){

        super(area, Orientation.DOWN, coordinates);
        sprite = new Sprite("rock.2", 1f, 1f, this);

        vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f),
                ANIMATION_DURATION / 7, false);
    }

    private static final float IMMUNITY_DURATION = 1.0f; // même valeur que player/logmonster pour l’instant
    private final Cooldown immunityCd = new Cooldown(IMMUNITY_DURATION);
    private boolean immune = false;
    private int blinkTick = 0;

    private void triggerImmunity() {
        immune = true;
        blinkTick = 0;
        immunityCd.reset();
    }

    public void weaken() {

        // if(recentlyHit || vanishing ){
        // return;
        // }
        // recentlyHit = true;

        if (vanishing || immune) {
            return;
        }

        healthBar.decrease(1);

        if (healthBar.isOff()) {
            vanishing = true;
            return;
        }

        // if (vanishing) return;
        // if(immune);

        // hP -= 1;
        // System.out.println("Rock hit ! remaining"+hP);

        // if (hP <= 0) {
        // vanishing = true;
        // return;
        // }

        triggerImmunity();

    }

    // public void resetHitFlag(){
    // recentlyHit = false;
    // }

    private void handleVanish(float dt) {
        vanishAnimation.update(dt);

        if (vanishAnimation.isCompleted()) {
            vanishAnimation.reset();

            // 1 chance sur 2 de drop un cœur
            if (RandomGenerator.rng.nextBoolean()) {
                getOwnerArea()
                        .registerActor(new Heart(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates()));

            }

            // Retirer le rocher de l’aire
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {

        boolean visible = !immune || (blinkTick % 2 == 0);

        if (!visible)
            return;

        if (vanishing) {
            vanishAnimation.draw(canvas);
        } else {
            sprite.draw(canvas);
            if (healthBar.isOn()) {
                healthBar.draw(canvas);
            }
        }

        // if(visible){
        // if(vanishing) vanishAnimation.draw(canvas);
        // else sprite.draw(canvas);
        // }
        //
        // if(!vanishing && healthBar.isOn()){
        // healthBar.draw(canvas);
        // }
        //
        // if (!visible) {
        // return;
        // // ton draw actuel du Rock (sprite/animation)
        // }
        //
        // if (vanishing) {
        // vanishAnimation.draw(canvas);
        // } else {
        // sprite.draw(canvas);
        // }
    }

    @Override
    public void update(float dt) {

        if (vanishing) {
            handleVanish(dt);
            return;
        }

        if (immune) {
            blinkTick++;
            if (immunityCd.ready(dt)) {
                immune = false;
            }
        }
        // super.update(dt);
    }

    @Override
    public boolean takeCellSpace() {
        return true; // NON traversable
    }

    @Override
    public boolean isCellInteractable() {
        return true; // Accepte interactions de contact
    }

    @Override
    public boolean isViewInteractable() {
        return true; // Accepte interactions à distance
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of(getCurrentMainCellCoordinates());
    }
}
