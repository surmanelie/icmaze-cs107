package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class MediumArea extends AireLabyrinthique{

    public MediumArea ( int keyId){
        super("MediumArea",16,AreaPortals.W,AreaPortals.E, keyId, Difficulty.HARDEST);
    }

    protected void createArea(){
        //super.createArea();
        //AregisterActor(new Background(this, getBehaviorName()));
        //maintenant on configure des portails pour BossArea

        //ICMazePlayer player = new ICMazePlayer(this, Orientation.DOWN, getplayerSpawnPosition(),"player");
        //registerActor(player);






        setWestState(Portal.State.OPEN);
        setEastState(Portal.State.OPEN);
        setNorthState(Portal.State.INVISIBLE);
        setSouthState(Portal.State.INVISIBLE);

        setWestDestination("icmaze/SmallArea["+ keyIdL2+"]");
        setEastDestination("icmaze/LargeArea["+ keyIdL4+"]");


        // il n'y a que pour west qu'on met une aire d'arrivée car les autres sont invisibles
        //setEastDestination("icmaze/Spawn");




    }
    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5,7);
    }

    @Override
    public String getTitle(){
        return "icmaze/MediumArea["+ keyIdL3+"]";
    }


}