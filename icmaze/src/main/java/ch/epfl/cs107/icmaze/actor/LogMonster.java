package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.LinkedList;
import java.util.Queue;

public class LogMonster  extends PathFinderEnnemy{

    private static final int MAX_HEALTH = 3;
    //à combien il peut voir autour de lui
    private static final int PERCEPTION_RADIUS = 5;

    private final double pTransition;

    public enum State {
        SLEEPING,
        RANDOM,
        TARGETING
    }

    private State state;


    private DiscreteCoordinates lastKnowPlayerPosition;

    private Orientation plannedOrientation;

    private final Cooldown reorientCooldown;
    private final Cooldown stateCooldown;

    private static final int DEATH_ANIMATION_DURATION = 24;
    private static final int ANIMATION_DURATION = 12;

    private OrientedAnimation targetingAnimation;
    private OrientedAnimation randomAnimation;
    private OrientedAnimation sleepingAnimation;

    private Path graphicPath;


    private final LogMonsterInteractionHandler handler = new LogMonsterInteractionHandler();

    public LogMonster (Area area, Orientation orientation, DiscreteCoordinates position, State initialState ){

        super(area, orientation, position, MAX_HEALTH, PERCEPTION_RADIUS);
        this.state = initialState;

        this.reorientCooldown = new Cooldown(0.75f);
        this.stateCooldown = new Cooldown(3.0f);

        int difficulty = Difficulty.MEDIUM; // valeur par défaut

        if (area instanceof AireLabyrinthique) {
            difficulty = ((AireLabyrinthique)area).getDifficulty();
        }

        this.pTransition = (double) Difficulty.HARDEST / (double) difficulty;

        //System.out.println("New LogMonster at "+position+" | state = "+initialState);

        Vector anchor = new Vector (-0.5f, 0.25f);

        Orientation[] ordersTargeting = {
                Orientation.DOWN,
                Orientation.RIGHT,
                Orientation.UP,
                Orientation.LEFT
        };

        targetingAnimation = new OrientedAnimation("icmaze/logMonster", ANIMATION_DURATION / 3, this, anchor, ordersTargeting, 4, 2, 2, 32, 32, true);

        Orientation[] ordersRandom = {
                Orientation.DOWN,
                Orientation.UP,
                Orientation.RIGHT,
                Orientation.LEFT
        };

        randomAnimation = new OrientedAnimation("icmaze/logMonster_random", ANIMATION_DURATION/3, this, anchor, ordersRandom, 4, 2, 2, 32, 32, true);

        Orientation[] ordersSleeping = {
                Orientation.DOWN,
                Orientation.LEFT,
                Orientation.UP,
                Orientation.RIGHT
        };
        sleepingAnimation = new OrientedAnimation("icmaze/logMonster.sleeping", ANIMATION_DURATION/3, this, anchor, ordersSleeping, 4, 2, 2, 32, 32, true);

    }



    @Override
    protected Animation createDeathAnimation() {
        return new Animation("icmaze/vanish", 7, 2, 2 ,this, 32, 32, new Vector(-0.5f, 0f), DEATH_ANIMATION_DURATION/7, false);

    }



    private Orientation leftof(Orientation o){
        return switch (o) {
            case UP -> Orientation.LEFT;
            case LEFT -> Orientation.DOWN;
            case DOWN -> Orientation.RIGHT;
            case RIGHT -> Orientation.UP;
            default -> o;
        };
    }

    @Override
    public void updateAlive(float deltaTime) {

        boolean canReorient = reorientCooldown.ready(deltaTime);
        boolean canChangeState = stateCooldown.ready(deltaTime);

        var rng = RandomGenerator.rng;

        plannedOrientation = null;

        switch (state) {

            case SLEEPING -> {
                if(canReorient){
                    orientate(leftof(getOrientation()));
                }

                if (canChangeState && rng.nextDouble() < pTransition) {
                    state = State.RANDOM;
                }
            }

            case RANDOM -> {
                if(canReorient) {
                    Orientation[] dirs = Orientation.values();
                    Orientation randomDir = dirs[rng.nextInt(dirs.length)];
                    plannedOrientation = randomDir;
                }

                if (canChangeState && lastKnowPlayerPosition != null && rng.nextDouble() < pTransition) {
                    state = State.TARGETING;
                }
            }

            case TARGETING -> {
                if(lastKnowPlayerPosition == null) {
                    state = State.RANDOM;
                }else{

                    if (canReorient) {
                        Orientation target = computeTargetOrientation();
                        if (target != null) {
                            plannedOrientation = target;
                        }
                    }

                    //il peut se rendormir
                    if (canChangeState && rng.nextDouble() < (1.0 - pTransition)){
                        state = State.SLEEPING;
                    }
                }
            }
        }

        switch (state) {
            case SLEEPING -> sleepingAnimation.update(deltaTime);
            case RANDOM -> randomAnimation.update(deltaTime);
            case TARGETING -> targetingAnimation.update(deltaTime);
        }
        System.out.println("State = "+state);

//        // on met à jour l'orientation planifiée en fonction de la dernière position connue du joueur
//        plannedOrientation = computeTargetOrientation();

        super.updateAlive(deltaTime);
    }

    // dans quelle direction on se déplace au prochain mouvement
    @Override
    public Orientation getNextOrientation() {
        return plannedOrientation;
    }

    //méthode pour calculer l'orientation pour avoir  le plus court chemin
    private Orientation computeTargetOrientation(){

        // si on a jamais vu le joueur ba on bouge pas
        if (lastKnowPlayerPosition == null){
            return null;
        }

        DiscreteCoordinates from = getCurrentMainCellCoordinates();

        ICMazeArea area = (ICMazeArea) getOwnerArea();

        Queue<Orientation> path = area.getShortestPath(from, lastKnowPlayerPosition);

        if(path == null || path.isEmpty()){
            lastKnowPlayerPosition = null;
            graphicPath = null;
            return  null;
        }

        graphicPath = new Path(this.getPosition(), new LinkedList<>(path));

        return path.poll(); // on prend juste la première direction du chemin

    }

    @Override
    public boolean wantsViewInteraction() {
        return state != State.SLEEPING;
    }


    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {

            if (state == State.SLEEPING) {
                return;
            }

            if(!isCellInteraction) {
                lastKnowPlayerPosition = player.getCurrentMainCellCoordinates();
                System.out.println("LogMonster a vu le joueur en "+ lastKnowPlayerPosition);

                DiscreteCoordinates front = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

                if (player.getCurrentMainCellCoordinates().equals(front)) {
                    player.sufferHit();
                }
            }



//            // la on mémorise la dernière position connue
//            if(!isCellInteraction) {
//                lastKnowPlayerPosition = player.getCurrentMainCellCoordinates();
//            }
        }
        // on a rien besoin d'autre car les autres méthodes gardent le même comportement que par défaut
    }

    public void setLastKnowPlayerPosition(DiscreteCoordinates position){
        this.lastKnowPlayerPosition = position;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        this.state = state;
    }

    @Override
    public void draw(Canvas canvas) {
        switch (state) {
            case SLEEPING -> sleepingAnimation.draw(canvas);
            case RANDOM -> randomAnimation.draw(canvas);
            case TARGETING -> targetingAnimation.draw(canvas);
        }

        if (graphicPath != null) {
            graphicPath.draw(canvas);
        }
    }
}
