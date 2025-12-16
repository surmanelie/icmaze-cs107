package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * SmallArea
 * Represents a small labyrinth area (size 8).
 */
public class SmallArea extends AireLabyrinthique {

    /**
     * SmallArea constructor
     * 
     * @param keyId (int): ID of the key generated in this area
     */
    public SmallArea(int keyId) {
        super("SmallArea", 8, AreaPortals.W, AreaPortals.E, keyId, Difficulty.HARDEST);
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    protected void createArea() {
        super.createArea();
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    public String getTitle() {
        return "icmaze/SmallArea[" + this.getKeyId() + "]";
    }

    @Override
    public int getKeyId() {
        return super.getKeyId();
    }
}
