package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.actor.collectable.Coin;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.keyIdL1;

/**
 * Spawn
 * The starting area of the game.
 */
public class Spawn extends ICMazeArea {

    private DialogHandler dialogHandler;
    private boolean welcomeShown = false;

    /**
     * Default Spawn constructor
     */
    public Spawn() {
        super("SmallArea", 8);
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }

    @Override
    protected void createArea() {

        Pickaxe pickaxe = new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5, 4));
        Heart heart = new Heart(this, Orientation.DOWN, new DiscreteCoordinates(4, 5));

        // Key 1: ID MAX_VALUE at (6,5)
        Key key1 = new Key(this, Orientation.DOWN, new DiscreteCoordinates(6, 5), keyIdL1);
        registerActor(key1);

        // Key 2: ID 0 at (1,2)
        Key key2 = new Key(this, Orientation.DOWN, new DiscreteCoordinates(1, 2), 0);
        registerActor(key2);

        registerActor(pickaxe);
        registerActor(heart);

        Rock rock = new Rock(this, new DiscreteCoordinates(3, 3));
        registerActor(rock);

    }

    @Override
    public void setGame(ICMaze game) {
        super.setGame(game);
        this.dialogHandler = game;
    }

    private boolean coinsSpawned = false;

    private int computeCoinCount(int kills) {
        if (kills >= 5)
            return 3;
        if (kills >= 3)
            return 2;
        return 1;
    }

    private void spawnCoins(int amount) {
        if (amount >= 1)
            registerActor(new Coin(this, Orientation.DOWN, new DiscreteCoordinates(5, 7), this));
        if (amount >= 2)
            registerActor(new Coin(this, Orientation.DOWN, new DiscreteCoordinates(4, 7), this));
        if (amount >= 3)
            registerActor(new Coin(this, Orientation.DOWN, new DiscreteCoordinates(6, 7), this));
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (!welcomeShown && dialogHandler != null) {
            dialogHandler.publish(new Dialog("welcome"));
            welcomeShown = true;
        }

        if (isOn() && !coinsSpawned) {
            int kills = getMonsterKillCount();
            int coinAmount = computeCoinCount(kills);
            spawnCoins(coinAmount);
            coinsSpawned = true;
        }
    }
}
