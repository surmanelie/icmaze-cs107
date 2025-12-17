package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * SmallArea
 * Represents a small labyrinth area (size 8).
 */
public class SmallArea extends AireLabyrinthique {

    /**
     * SmallArea constructor
     * 
     * @param keyId      (int): ID of the key generated in this area
     * @param difficulty (int): Difficulty level (minimum room size)
     */
    public SmallArea(int keyId, int difficulty) {
        super("SmallArea", 8, AreaPortals.W, AreaPortals.E, keyId, difficulty);
    }

    @Override
    public int getSize() {
        return 8;
    }

    // Removed createArea() to reuse AireLabyrinthique's implementation

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
