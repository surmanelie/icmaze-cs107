package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.ui.CoinCounterHUD;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;
import ch.epfl.cs107.play.window.Keyboard;

import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.*;

import ch.epfl.cs107.icmaze.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.window.Canvas;

public class ICMaze extends AreaGame implements DialogHandler {

    private static final String INITIAL_AREA = "icmaze/Spawn";
    private ICMazePlayer player;
    private final CoinCounterHUD coinHUD = new CoinCounterHUD();
    private Dialog activeDialog;
    private int monsterKillCount = 0;

    private Window window;
    private FileSystem fileSystem;

    // ... imports ...

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        this.window = window;
        this.fileSystem = fileSystem;
        this.monsterKillCount = 0;

        if (!super.begin(window, fileSystem)) {
            return false;
        }

        createAreas();
        initArea(INITIAL_AREA);

        return true;
    }

    private void createAreas() {
        // generateHardCodedLevel();
        ICMazeArea[] areas = LevelGenerator.generateLine(this, 2);

        if (areas.length > 0) {
            // Le dernier est BossArea qui est le signal logique
            ch.epfl.cs107.play.signal.logic.Logic bossSignal = (ch.epfl.cs107.play.signal.logic.Logic) areas[areas.length
                    - 1];

            for (ICMazeArea area : areas) {
                area.setValidationSignal(bossSignal);
                addArea(area);
            }
        }
    }

    // ...

    @Override
    public void update(float deltaTime) {

        // on vérifie ici la touche reset
        Keyboard keyboard = getCurrentArea().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_GAME).isPressed()) {
            resetGame();
            return;
        }

        // Check active dialog
        if (activeDialog != null) {

            // Should be drawn
            // Check if completion occurred (empty dialog or finished)
            if (activeDialog.isCompleted()) {
                activeDialog = null;
                return;
            }

            // Only update active dialog if next key is pressed
            if (keyboard.get(KeyBindings.NEXT_DIALOG).isPressed()) {
                activeDialog.update(deltaTime);
                if (activeDialog.isCompleted()) {
                    activeDialog = null;
                }
            }
            return; // Pause game update
        }

        super.update(deltaTime);

        if (player.getisChanging()) {
            changeArea(player.getDestinationArea(), player.getDestinationCoordonates());
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (player != null) {
            coinHUD.setCoinCount(player.getCoinCount());
            coinHUD.draw(getWindow());
        }
        if (activeDialog != null) {
            activeDialog.draw(getWindow());
        }
    }

    @Override
    public void publish(Dialog dialog) {
        this.activeDialog = dialog;
    }

    // ... resetGame and other methods ...

    public void resetGame() {
        begin(window, fileSystem);
    }

    public void resetCurrentArea() {
        String currentAreaKey = getCurrentArea().getTitle();
        player.leaveArea();

        ICMazeArea area = (ICMazeArea) setCurrentArea(currentAreaKey, true);
        area.setGame(this); // on recharge l'aire courante

        // on recrée le joueur
        DiscreteCoordinates spawnPosition = area.getplayerSpawnPosition();
        player.resetAfterAreaReset();
        player.enterArea(area, spawnPosition);
        area.setViewCandidate(player);
    }

    // ... helper methods ...

    private void changeArea(String destination, DiscreteCoordinates coordinates) {

        player.leaveArea();
        ICMazeArea newArea = (ICMazeArea) setCurrentArea(destination, false);
        newArea.setGame(this);
        player.enterArea(newArea, coordinates);
        newArea.setViewCandidate(player);
        player.setisChanging(false);
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
        area.setViewCandidate(player);

    }

    // private void generateHardCodedLevel() {
    // Keep empty reference or remove if unused, but maintain structure for now
    // }

    public void incrementMonsterKillCount() {
        monsterKillCount++;
    }

    public int getMonsterKillCount() {
        return monsterKillCount;
    }
}
