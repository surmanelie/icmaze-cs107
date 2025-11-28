package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;

public class BossArea extends ICMazeArea {

    public BossArea(){
        super("SmallArea");
    }

    @Override
    public String getTitle(){
        return "icmaze/Boss";
    }

}
