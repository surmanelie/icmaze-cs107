package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Spawn extends ICMazeArea {


    public Spawn(){
        super("SmallArea",8);
    }

    @Override
    public String getTitle(){
        return "icmaze/Spawn";
    }

    @Override
    protected void createArea(){
        registerActor(new Background(this, getBehaviorName()));
        //registerActor(new Background(this));
        //registerActor(new Foreground(this));

        ICMazePlayer player  = new ICMazePlayer(this, Orientation.DOWN, new DiscreteCoordinates(5,7), "icmaze/player",KeyBindings.PLAYER_KEY_BINDINGS);
        Pickaxe pickaxe = new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5,4));
        Heart heart = new Heart(this,new DiscreteCoordinates(4,5) );
        // Clé 1 : identifiant MAX_VALUE en (6,5)
        Key key1 = new Key(this, Orientation.DOWN, new DiscreteCoordinates(6, 5), Integer.MAX_VALUE);
        registerActor(key1);

// Clé 2 : identifiant MAX_VALUE - 1 en (1,2)
        Key key2 = new Key(this, Orientation.DOWN, new DiscreteCoordinates(1, 2), Integer.MAX_VALUE - 1);
        registerActor(key2);

        registerActor(pickaxe);
        registerActor(heart);

        registerActor(player);
        createPortals();

        setEastState(Portal.State.OPEN);
        setSouthState(Portal.State.OPEN);
        setNorthState(Portal.State.OPEN);
        setWestState(Portal.State.OPEN);

        setNorthDestination("icmaze/Boss");
;



    }

}
