package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class SmallArea extends AireLabyrinthique {

    // private int sizeSmall;
    // public int getsSizeSmall(){
    // return sizeSmall; }

    // @Override
    // public DiscreteCoordinates arrivalCoordinates(int size) {
    // return ;
    // }
    //
    // @Override
    // public DiscreteCoordinates startingCoordinates(int size) {
    // return ;
    // }

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
        // AregisterActor(new Background(this, getBehaviorName()));
        // maintenant on configure des portails pour BossArea

        // ICMazePlayer player = new ICMazePlayer(this, Orientation.DOWN,
        // getplayerSpawnPosition(),"player");
        // registerActor(player);

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
