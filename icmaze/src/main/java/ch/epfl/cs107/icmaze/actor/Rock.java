package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
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
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Rock  extends AreaEntity implements Interactable, Updatable {

    // Sprite principal du rocher
    private Sprite sprite;

    // Points de vie restants (le rocher disparaît à 0)
    private int hP = 3;

    // Indique si le rocher est en train de disparaître
    private boolean vanishing = false;

    // Animation de disparition
    private static final int ANIMATION_DURATION = 24;
    private final Animation vanishAnimation;


    // Initialise le rocher, son sprite et l’animation de disparition

    public Rock(Area area, DiscreteCoordinates coordinates){
        super(area, Orientation.DOWN,coordinates);
        sprite = new Sprite(
                "rock.2",
                1f,
                1f,
                this);

        vanishAnimation = new Animation(
                "icmaze/vanish",
                7, 2, 2,
                this,
                32, 32,
                new Vector(-0.5f, 0f),
                ANIMATION_DURATION / 7,
                false
        );
    }


    public void weaken() {
        hP -= 1;
        if (hP <= 0) {
            vanishing = true;
        }
    }

    private void handleVanish(float dt) {
        vanishAnimation.update(dt);

        if (vanishAnimation.isCompleted()) {
            vanishAnimation.reset();

            // 1 chance sur 2 de drop un cœur
            if (RandomGenerator.rng.nextBoolean()) {
                getOwnerArea().registerActor(new Heart(getOwnerArea(),Orientation.DOWN,getCurrentMainCellCoordinates()));

            }

            // Retirer le rocher de l’aire
            getOwnerArea().unregisterActor(this);
        }
    }




    @Override
    public void draw(Canvas canvas) {
        if (vanishing) {
            vanishAnimation.draw(canvas);
        } else {
            sprite.draw(canvas);
        }
    }

    @Override
    public void update(float dt) {
        if (vanishing) {
            handleVanish(dt);
        }
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

