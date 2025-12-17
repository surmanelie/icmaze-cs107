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
import ch.epfl.cs107.icmaze.actor.collectable.SpeedBall;

/**
 * ICMazeInteractionVisitor
 * InteractionVisitor for the ICMaze entities
 */
public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {

    /**
     * Simulate an interaction between ICMaze actors and an ICMazeCell
     * 
     * @param cell              (ICMazeCell): the cell, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(ICMazeBehavior.ICMazeCell cell, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and an ICMazePlayer
     * 
     * @param player            (ICMazePlayer): the player, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(ICMazePlayer player, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Pickaxe
     * 
     * @param pickaxe           (Pickaxe): the pickaxe, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Heart
     * 
     * @param heart             (Heart): the heart, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Heart heart, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Key
     * 
     * @param key               (Key): the key, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Key key, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Portal
     * 
     * @param portal            (Portal): the portal, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Portal portal, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Rock
     * 
     * @param rock              (Rock): the rock, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Rock rock, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a LogMonster
     * 
     * @param monster           (LogMonster): the monster, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(LogMonster monster, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Boss
     * 
     * @param boss              (Boss): the boss, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Boss boss, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a Coin
     * 
     * @param coin              (Coin): the coin, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(Coin coin, boolean isCellInteraction) {
        // default empty
    }

    /**
     * Simulate an interaction between ICMaze actors and a SpeedBall
     *
     * @param ball              (SpeedBall): the speed ball, not null
     * @param isCellInteraction (boolean): true if it is a cell interaction
     */
    default void interactWith(SpeedBall ball, boolean isCellInteraction) {
        // default empty
    }
}
