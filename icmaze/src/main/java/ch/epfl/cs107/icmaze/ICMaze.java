package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {

    private static final String INITIAL_AREA = "icmaze/Spawn";
    // TO BE COMPLETED
    private void createAreas() {

        BossArea boss = new BossArea();
        Spawn spawn = new Spawn();

        addArea(boss);
        addArea(spawn);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

       // super.begin(window,getFileSystem());
        // setCurrentArea(INITIAL_AREA,false);

        if (!super.begin(window, fileSystem)) {
            return false;
        }

        createAreas();
        setCurrentArea(INITIAL_AREA, true);


        return true;
    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }
}