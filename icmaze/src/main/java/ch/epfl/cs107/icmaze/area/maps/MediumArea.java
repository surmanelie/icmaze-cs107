package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * MediumArea
 * Represents a medium labyrinth area (size 16).
 */
public class MediumArea extends LabyrinthArea {

    /**
     * MediumArea constructor
     * 
     * @param keyId      (int): ID of the key generated in this area
     * @param difficulty (int): Difficulty level (minimum room size)
     */
    public MediumArea(int keyId, int difficulty) {
        super("MediumArea", 16, AreaPortals.W, AreaPortals.E, keyId, difficulty);
    }

    @Override
    public int getSize() {
        return 16;
    }

    // Removed createArea() to reuse AireLabyrinthique's implementation

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    public String getTitle() {
        return "icmaze/MediumArea[" + getKeyId() + "]";
    }
}