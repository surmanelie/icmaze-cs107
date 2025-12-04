package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.keyIdL4;

public class BossArea extends ICMazeArea {


    AreaPortals areaPortals;

    public BossArea(){
        super("SmallArea",8);
        //this.areaPortals=areaPortals;
    }

    @Override
    public String getTitle(){
        return "icmaze/Boss";
    }

    @Override
    protected void createArea(){
        //super.createArea();
        //AregisterActor(new Background(this, getBehaviorName()));
        //maintenant on configure des portails pour BossArea

        //ICMazePlayer player = new ICMazePlayer(this, Orientation.DOWN, getplayerSpawnPosition(),"player");
        //registerActor(player);

        setWestState(Portal.State.OPEN);
        setEastState(Portal.State.INVISIBLE);
        setNorthState(Portal.State.INVISIBLE);
        setSouthState(Portal.State.INVISIBLE);

        setWestDestination("icmaze/LargeArea["+ keyIdL4+"]"); // il n'y a que pour west qu'on met une aire d'arrivée car les autres sont invisibles
        setEastDestination("icmaze/Spawn");
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5,7);
    }
}
