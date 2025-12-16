package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * MediumArea
 * Represents a medium labyrinth area (size 16).
 */
public class MediumArea extends AireLabyrinthique {

    /**
     * MediumArea constructor
     * 
     * @param keyId (int): ID of the key generated in this area
     */
    public MediumArea(int keyId) {
        super("MediumArea", 16, AreaPortals.W, AreaPortals.E, keyId, Difficulty.HARDEST);
    }

    @Override
    public int getSize() {
        return 16;
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
        return "icmaze/MediumArea[" + getKeyId() + "]";
    }
}