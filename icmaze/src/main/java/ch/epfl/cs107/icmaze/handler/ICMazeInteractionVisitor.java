package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
/**
 * InteractionVisitor for the ICMaze entities
 */

public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {
  /// Add Interaction method with all non Abstract Interactable




        // Interaction avec une cellule
        default void interactWith(ICMazeBehavior.ICMazeCell cell, boolean isCellInteraction) {
            // rien par défaut
        }

        // Interaction avec le joueur
        default void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            // rien par défaut
        }

        // Interaction avec une pioche
        default void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            // rien par défaut
        }

        // Interaction avec un cœur
        default void interactWith(Heart heart, boolean isCellInteraction) {
            // rien par défaut
        }

        default void interactWith(Key key, boolean isCellInteraction) {

        }
        default void interactWith(Portal portal, boolean isCellInteraction) {}

}
