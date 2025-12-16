package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.ICMazeBehavior;
import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.collectable.Coin;

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

    default void interactWith(Portal portal, boolean isCellInteraction) {

    }

    default void interactWith(Rock rock, boolean isCellInteraction) {

    }

    default void interactWith(LogMonster monster, boolean isCellInteraction) {

    }

    default void interactWith(Boss boss, boolean isCellInteraction) {

    }

    default void interactWith(Coin coin, boolean isCellInteraction) {
    }

}
