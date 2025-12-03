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
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5,7);
    }

    @Override
    public String getTitle(){
        return "icmaze/Spawn";
    }

    @Override
    protected void createArea(){
        //on supprime ce qu'il y a en bas car ICMazeArea le fait déjà donc pas besoin de le refaire ici, il n'y a rien de spécifique à faire pour créer ue aire dans Spawn par rapport à la méthode dans Area
        //registerActor(new Background(this, getBehaviorName()));
        //registerActor(new Foreground(this));

        //ICMazePlayer player  = new ICMazePlayer(this, Orientation.DOWN, getplayerSpawnPosition(), "player");
        //registerActor(player);

        // il n'y avait pas besoin de remettre un joueur car on crée que un dans le begin, sinon à chaque fois qu'on revient dans le Spawn un nouveau joueur est crée et reokgister


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


        setEastState(Portal.State.OPEN);
        setNorthState(Portal.State.INVISIBLE);
        setWestState(Portal.State.INVISIBLE);
        setSouthState(Portal.State.INVISIBLE);
        //createPortals();
        //pas besoin de créer des portails ici care ICMazeArea le fait deja


        //setNorthDestination("icmaze/Boss");
        setEastDestination("icmaze/Boss");
        //setSouthDestination("icmaze/Boss");
        // car pour l'instant on en a pas besoin
        //setWestDestination("icmaze/Boss");

    }

}
