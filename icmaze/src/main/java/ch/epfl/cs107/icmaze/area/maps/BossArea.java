package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.actor.Boss;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

//import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.//keyIdL4;

public class BossArea extends ICMazeArea {

    // private int sizeBoss;
    // public int getsSizeBoss(){
    // return sizeBoss; }

    // @Override
    // public DiscreteCoordinates arrivalCoordinates(int size) {
    // return ;
    // }
    //
    // @Override
    // public DiscreteCoordinates startingCoordinates(int size) {
    // return ;
    // }

    @Override
    public int getSize() {
        return 8;
    }

    AreaPortals areaPortals;

    public BossArea() {
        super("SmallArea", 8);
        // this.areaPortals=areaPortals;
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }

    @Override
    protected void createArea() {
        registerActor(new Boss(this, Orientation.DOWN, new DiscreteCoordinates(4, 4)));
//        registerActor(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5, 5)));
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }
}
