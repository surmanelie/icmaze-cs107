package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

/**
 * ICMazeBehavior
 * Defines the behavior of the ICMaze cells (walkability, interaction).
 */
public class ICMazeBehavior extends AreaBehavior {

    /**
     * ICMazeBehavior constructor
     * 
     * @param window (Window): The game window
     * @param name   (String): The name of the behavior map file
     */
    public ICMazeBehavior(Window window, String name) {
        super(window, name);

        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {

                int color = getRGB(getHeight() - 1 - y, x);
                CellType ct = CellType.toType(color);

                setCell(x, y, new ICMazeCell(x, y, ct));
            }
        }
    }

    /**
     * CellType
     * Enumeration of the different cell types in the maze behaviors.
     */
    public enum CellType {
        NONE(0, false),
        GROUND(-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true);

        final int type;
        final boolean walkable;

        CellType(int type, boolean walkable) {
            this.type = type;
            this.walkable = walkable;
        }

        public static CellType toType(int rgb) {
            for (CellType t : values()) {
                if (t.type == rgb) {
                    return t;
                }
            }
            return NONE;
        }
    }

    /**
     * ICMazeCell
     * Represents a single cell in the maze with specific behavior.
     */
    public class ICMazeCell extends AreaBehavior.Cell implements Interactable {
        private final CellType type;

        /**
         * ICMazeCell constructor
         * 
         * @param x    (int): x coordinate
         * @param y    (int): y coordinate
         * @param type (CellType): The type of the cell
         */
        public ICMazeCell(int x, int y, CellType type) {
            super(x, y);
            this.type = type;
        }

        @Override
        public boolean takeCellSpace() {
            return false;
        }

        @Override
        public boolean canEnter(Interactable entity) {
            if (!type.walkable)
                return false;

            // Check if any other entity in the cell takes space
            if (entity.takeCellSpace()) {
                for (Interactable other : entities) {
                    if (other.takeCellSpace()) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
        }
    }
}
