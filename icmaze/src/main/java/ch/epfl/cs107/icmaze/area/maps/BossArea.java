package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.actor.Boss;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

//import static ch.epfl.cs107.icmaze.area.maps.AireLabyrinthique.//keyIdL4;

import ch.epfl.cs107.play.signal.logic.Logic;

public class BossArea extends ICMazeArea implements Logic {

    private Boss boss;

    @Override
    public boolean isOn() {
        return boss != null && boss.isDefeated() && boss.getDroppedKey() != null && boss.getDroppedKey().isCollected();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    // private int sizeBoss;
    // public int getsSizeBoss(){
    // return sizeBoss; }

    // @Override
    // public DiscreteCoordinates arrivalCoordinates(int size) {
    // return ;
    // }
    //
    // @Override
    // public DiscreteCoordinates startingCoordinates(int size) {
    // return ;
    // }

    @Override
    public int getSize() {
        return 8;
    }

    AreaPortals areaPortals;

    public BossArea() {
        super("SmallArea", 8);
        // this.areaPortals=areaPortals;
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }

    @Override
    protected void createArea() {
        // d'abord on nettoie l'ancien boss
//        if (boss != null){
//            boss.cleanUP(this);
//        }
        boss = new Boss(this, Orientation.DOWN, new DiscreteCoordinates(4, 4));
        registerActor(boss);
        // registerActor(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5,
        // 5)));
    }

    @Override
    public DiscreteCoordinates getplayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }
//
//    @Override
//    public float getCameraScaleFactor() {
//        return 15f; // ou 14f, ou 16f — constant
//    }

}
