package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public abstract class ICMazeArea extends Area {
    private final String behaviorName;

    //extends Area ?

    public ICMazeArea(String behaviorName ) {  // validation: est -ce que c'est bien ça la modif à faire dans le 2.2 par rapport au tutoriel pour avoir plusieurs noms
        super();
        this.behaviorName = behaviorName;
    }

    @Override
    public float getCameraScaleFactor(){
        return 20f;
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem){
        super.begin(window,fileSystem);
        setBehavior(new ICMazeBehavior(window, behaviorName));
        createArea();
        return true;
    }
    //override
    protected void createArea(){
        registerActor(new Background(this, behaviorName));
    }
}
