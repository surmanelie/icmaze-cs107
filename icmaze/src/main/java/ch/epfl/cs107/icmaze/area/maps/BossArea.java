package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class BossArea extends ICMazeArea {
    AreaPortals areaPortals;

    public BossArea(){
        super("SmallArea",8);
        this.areaPortals=areaPortals;
    }

    @Override
    public String getTitle(){
        return "icmaze/Boss";
    }

    @Override
    protected void createArea(){
        //super.createArea();
        registerActor(new Background(this, getBehaviorName()));
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5,7);
    }
}
