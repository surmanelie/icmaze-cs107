package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * LargeArea
 * Represents a large labyrinth area (size 32).
 */
public class LargeArea extends AireLabyrinthique {

    /**
     * LargeArea constructor
     * 
     * @param keyId      (int): ID of the key generated in this area
     * @param difficulty (int): Difficulty level (minimum room size)
     */
    public LargeArea(int keyId, int difficulty) {
        super("LargeArea", 32, AreaPortals.W, AreaPortals.E, keyId, difficulty);
    }

    @Override
    public int getSize() {
        return 32;
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
        return "icmaze/LargeArea[" + getKeyId() + "]";
    }
}