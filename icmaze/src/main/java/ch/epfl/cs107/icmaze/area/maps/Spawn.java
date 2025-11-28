package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Spawn extends ICMazeArea {


    public Spawn(){
        super("SmallArea");
    }

    @Override
    public String getTitle(){
        return "icmaze/Spawn";
    }

    @Override
    protected void createArea(){
        super.createArea();
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ICMazePlayer player  = new ICMazePlayer(this, Orientation.DOWN, new DiscreteCoordinates(5,7), "icmaze/player",KeyBindings.PLAYER_KEY_BINDINGS);

        registerActor(player);
    }

}
