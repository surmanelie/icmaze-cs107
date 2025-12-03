package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {

    private static final String INITIAL_AREA = "icmaze/Spawn";
    private ICMazePlayer player;

    private void createAreas() {
        addArea(new Spawn());
        addArea(new BossArea());
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        if (!super.begin(window, fileSystem)) {
            return false;
        }
        addArea(new Spawn());
        addArea(new BossArea());
        initArea(INITIAL_AREA);


//        setCurrentArea(INITIAL_AREA, true);

        return true;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(player.getisChanging()){
            changeArea(player.getDestinationArea(), player.getDestinationCoordonates());
        }
    }



    private void changeArea(String destination,DiscreteCoordinates coordinates){
        System.out.println("Teleport to " + destination + " at " + coordinates);
        player.leaveArea();
        ICMazeArea newArea = (ICMazeArea) setCurrentArea(destination,false);
        player.enterArea(newArea,coordinates);
        player.setisChanging(false); //ici on change l etat de changement
    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }

    private void initArea(String areaKey) {
        ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, false);
        DiscreteCoordinates spawnPosition = area.getplayerSpawnPosition();
        player = new ICMazePlayer(area, Orientation.DOWN, spawnPosition, "icmaze/player");
        player.enterArea(area, spawnPosition);

        // player.centerCamera();
    }
}
