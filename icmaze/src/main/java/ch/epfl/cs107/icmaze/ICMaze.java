package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.play.window.Keyboard;

import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.*;

public class ICMaze extends AreaGame {


    private static final String INITIAL_AREA = "icmaze/Spawn";
    private ICMazePlayer player;

    private Window window;
    private FileSystem fileSystem;



    private void createAreas() {
        generateHardCodedLevel();

    }


    private  void generateHardCodedLevel(){
        Spawn a0 = new Spawn();
        addArea(a0);
        SmallArea a1 = new SmallArea(AireLabyrinthique.keyIdL2);
        addArea(a1);
        MediumArea a2 = new MediumArea(AireLabyrinthique.keyIdL3);
        addArea(a2);
        LargeArea a3 = new LargeArea(AireLabyrinthique.keyIdL4);
        addArea(a3);
        BossArea a4 = new BossArea();
        addArea(a4);

//        addArea(new SmallArea(AireLabyrinthique.keyIdL2));
//        addArea(new MediumArea(AireLabyrinthique.keyIdL3));
//        addArea(new LargeArea(AireLabyrinthique.keyIdL4));
        a0.setEastDestination("icmaze/SmallArea["+ keyIdL2 +"]",8);

        a1.setEastDestination("icmaze/MediumArea["+ keyIdL3 +"]",16);
        a1.setWestDestination("icmaze/Spawn",8);

        a2.setEastDestination("icmaze/LargeArea["+ keyIdL4+"]",32);
        a2.setWestDestination("icmaze/SmallArea["+ keyIdL2 +"]",8);

        a3.setEastDestination("icmaze/Boss",8);
        a3.setWestDestination("icmaze/MediumArea["+ keyIdL3 +"]",16);

        a4.setEastDestination("icmaze/Spawn",8);
        a4.setWestDestination("icmaze/LargeArea["+ keyIdL4+"]",32);


//        a0.setEState(lock)
//
//        a0.setEastDestination //car Spawn envoie dans Small
//        a1.setDestination(16); // car Small envoie dans Medium
//        a2.setDestination(32); //  car Medium envoie dans Large
//        a3.setDestination(8); // car Large envoie dans Boss
//        a4.setDestination(8); // car Boss envoie dans Spawn


    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        this.window = window;
        this.fileSystem = fileSystem;

        if (!super.begin(window, fileSystem)) {
            return false;
        }

        createAreas();

//        // ce qui est juste après je suis pas sur du tout
//        for (Area area :areas) {
//            if(area instanceof ICMazeArea icArea){
//                icArea.setGame(this);
//            }
//        }


        initArea(INITIAL_AREA);



        return true;
    }

    public void resetGame(){
        begin(window, fileSystem);
    }

    public void resetCurrentArea(){
        String currentAreaKey = getCurrentArea().getTitle();
        player.leaveArea();

        ICMazeArea area = (ICMazeArea) setCurrentArea(currentAreaKey, true);
        area.setGame(this); //on recharge l'aire courante

        // on recrée le joueur
        DiscreteCoordinates spawnPosition = area.getplayerSpawnPosition();
        player.resetAfterAreaReset();
        // justement on ne crée pas de nouveau player = new ICMazePlayer(area, Orientation.DOWN, spawnPosition, "icmaze/player");
        player.enterArea(area, spawnPosition);

//        System.out.println("ResetCurrentArea: area=" + currentAreaKey);
//        System.out.println("SpawnPosition=" + spawnPosition);
    }



    @Override
    public void update(float deltaTime) {

        //on vérifie ici la touche reset
        Keyboard keyboard = getCurrentArea().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_GAME).isPressed()){
            resetGame();
            return ;
        }

//        if (player.isDead()){
//            resetGame();
//            return;
//        }

        super.update(deltaTime);

        if(player.getisChanging()){
            changeArea(player.getDestinationArea(), player.getDestinationCoordonates());
        }
    }



    private void changeArea(String destination,DiscreteCoordinates coordinates){
        System.out.println("Teleport to " + destination + " at " + coordinates);
        player.leaveArea();
        ICMazeArea newArea = (ICMazeArea) setCurrentArea(destination,false);
        newArea.setGame(this);
        player.enterArea(newArea,coordinates);
        player.setisChanging(false); //ici on change l etat de changement
    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }

    private void initArea(String areaKey) {
        ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, false);
        area.setGame(this);

        DiscreteCoordinates spawnPosition = area.getplayerSpawnPosition();
        player = new ICMazePlayer(area, Orientation.DOWN, spawnPosition, "icmaze/player");
        player.enterArea(area, spawnPosition);


    }
}
