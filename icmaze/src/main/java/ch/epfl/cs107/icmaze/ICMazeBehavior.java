package ch.epfl.cs107.icmaze;


import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

public class ICMazeBehavior extends AreaBehavior{

    public ICMazeBehavior (Window window, String name){
        super (window,name);

        for (int y =0; y < getHeight(); ++y){
            for (int x = 0; x < getWidth(); ++x){

                int color = getRGB(getHeight() - 1 - y, x);
                CellType ct = CellType.toType(color);

                setCell (x, y, new ICMazeCell(x,y,ct));
            }
        }
    }

    public enum CellType{
        NONE(0,false),
        GROUND(-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true);

        final int type; // c'est quoi concrètement type
        final boolean walkable;

        CellType(int type, boolean walkable){
            this.type = type;
            this.walkable = walkable;
        }
        public static CellType toType(int rgb){
            for (CellType t : values()){
                if(t.type == rgb){
                    return t;
                }
            }
            return NONE;
        }
    }





    public class ICMazeCell extends AreaBehavior.Cell implements Interactable{
        boolean walkable = false;
        private final CellType type;

        public ICMazeCell(int x, int y, CellType type){
            super(x,y);
            this.type = type;

        }


        @Override
        public boolean takeCellSpace(){ // à vérifier si c'est bien publique
            return false;
        }


        @Override
        public boolean canEnter(Interactable entity){
            if(!type.walkable)return false;
            // si y'a rien sur la cellule il peut passer le bg
            if(entity.takeCellSpace()) {// ici on vérifie que dans chaque cellule il n'y ait personne d'autre
                for(Interactable other : entities) {
                    if (other.takeCellSpace()){//takeCellSpace est la méthode qui dit si l'entité qui est dans la cellule prend ou pas de l'espace
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean canLeave(Interactable entity){ // est ce que l'entité a le droit de sortir de cette cellule
            return true;
        }

        @Override
        public boolean isCellInteractable(){ // est ce qu'on peut intéragir avec cette cellule par contact
            return true ;
        }

        @Override
        public boolean isViewInteractable(){ // est ce qu'on peut intéragir avec cette cellule à distance
            return false ;// false parce que les cellules n'ont pas d'action à distance
        }
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICMazeInteractionVisitor.ICMazeInteractionHandler) v).interactWith(this, isCellInteraction);
        }
    }


}
