package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Boss extends Ennemy {

    private static final int MAX_HEALTH = 5;
    private static final int BOSS_DAMAGE = 1;
    private boolean activated = false;

    private final OrientedAnimation idleAnimation;
    private final Cooldown barrageCooldown;
    private static final float BARRAGE_INTERVAL = 3.0f;

    private boolean keyDropped = false;

    private final BossInteractionHandler handler = new BossInteractionHandler();

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HEALTH);

        Vector anchor = new Vector(-0.5f, 0f);

        // Correct Animation Constructor based on LogMonster?
        // OrientedAnimation(name, frameDuration, parent, anchor, orders, frameCount,
        // width, height, regionWidth, regionHeight, repeat)

        Orientation[] orders = { Orientation.DOWN, Orientation.LEFT, Orientation.UP, Orientation.RIGHT };

        this.idleAnimation = new OrientedAnimation("icmaze/boss", 12, this,
                anchor, orders, 3, 2, 2, 32, 32, true);

        this.barrageCooldown = new Cooldown(BARRAGE_INTERVAL);
    }

    @Override
    public void updateAlive(float deltaTime) {
        if (!activated) {
            idleAnimation.update(deltaTime);
            return;
        }

        idleAnimation.update(deltaTime); // Update animation if activated

        if (barrageCooldown.ready(deltaTime)) {
            shootBarrage();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead()) {
            idleAnimation.draw(canvas);
        }
        super.draw(canvas);
    }

    @Override
    protected Animation createDeathAnimation() {
        return new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f), 24 / 7, false);
    }

    public void sufferHit() {
        if (isDead())
            return;

        if (!activated) {
            activated = true;
            teleport();
        } else {
            loseHealth(BOSS_DAMAGE);
            if (!isDead()) {
                teleport();
            } else {
                dropKey();
            }
        }
    }

    private void dropKey() {
        if (keyDropped)
            return;
        keyDropped = true;
        Key key = new Key(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates(), 999);
        getOwnerArea().registerActor(key);
        // Position and Owner are set in constructor
    }

    private void teleport() {
        Area area = getOwnerArea();
        int width = area.getWidth();
        int height = area.getHeight();

        List<TeleportDest> potential = new ArrayList<>();
        potential.add(new TeleportDest(new DiscreteCoordinates(width / 2, height / 2), Orientation.DOWN));
        potential.add(new TeleportDest(new DiscreteCoordinates(width / 2, height - 2), Orientation.DOWN));
        potential.add(new TeleportDest(new DiscreteCoordinates(width / 2, 1), Orientation.UP));
        potential.add(new TeleportDest(new DiscreteCoordinates(1, height / 2), Orientation.RIGHT));
        potential.add(new TeleportDest(new DiscreteCoordinates(width - 2, height / 2), Orientation.LEFT));

        DiscreteCoordinates current = getCurrentMainCellCoordinates();
        potential.removeIf(p -> p.coords.equals(current));

        if (!potential.isEmpty()) {
            TeleportDest dest = potential.get(RandomGenerator.rng.nextInt(potential.size()));
            respawnAt(area, dest);
        }
    }

    private void respawnAt(Area area, TeleportDest dest) {
        // Create new Boss instance to avoid grid ghosting issues with deferred
        // unregistration
        Boss newBoss = new Boss(area, dest.orientation, dest.coords);

        // Transfer state
        newBoss.activated = true;
        int damageTaken = MAX_HEALTH - this.getCurrentHealth();
        if (damageTaken > 0) {
            newBoss.silentDamage(damageTaken);
        }

        // Switch actors
        this.leaveArea();
        newBoss.enterArea(area, dest.coords);
    }

    // Helper to apply damage without triggering side effects/animations if needed
    private void silentDamage(int amount) {
        // Direct call to super loseHealth is safe as it only updates health/state
        super.loseHealth(amount);
    }

    private void shootBarrage() {
        DiscreteCoordinates pos = getCurrentMainCellCoordinates();
        Orientation ori = getOrientation();
        Area area = getOwnerArea();

        List<DiscreteCoordinates> targets = new ArrayList<>();

        if (ori == Orientation.UP || ori == Orientation.DOWN) {
            int targetY = pos.y + (int) ori.toVector().y;
            for (int x = 1; x < area.getWidth() - 1; x++) {
                targets.add(new DiscreteCoordinates(x, targetY));
            }
        } else {
            int targetX = pos.x + (int) ori.toVector().x;
            for (int y = 1; y < area.getHeight() - 1; y++) {
                targets.add(new DiscreteCoordinates(targetX, y));
            }
        }

        if (!targets.isEmpty()) {
            targets.remove(RandomGenerator.rng.nextInt(targets.size()));
        }

        for (DiscreteCoordinates target : targets) {
            new FireProjectile(area, ori, target).enterArea(area, target);
        }
    }

    private record TeleportDest(DiscreteCoordinates coords, Orientation orientation) {
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor visitor) {
            visitor.interactWith(this, isCellInteraction);
        }
    }

    private class BossInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
        }
    }
}
