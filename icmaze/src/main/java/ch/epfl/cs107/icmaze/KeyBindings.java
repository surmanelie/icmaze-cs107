package ch.epfl.cs107.icmaze;

import static ch.epfl.cs107.play.window.Keyboard.E;
import static ch.epfl.cs107.play.window.Keyboard.ENTER;
import static ch.epfl.cs107.play.window.Keyboard.P;
import static ch.epfl.cs107.play.window.Keyboard.R;
import static ch.epfl.cs107.play.window.Keyboard.B;
import static ch.epfl.cs107.play.window.Keyboard.SPACE;
import static ch.epfl.cs107.play.window.Keyboard.UP;
import static ch.epfl.cs107.play.window.Keyboard.DOWN;
import static ch.epfl.cs107.play.window.Keyboard.LEFT;
import static ch.epfl.cs107.play.window.Keyboard.RIGHT;

/**
 * KeyBindings
 * Class defining key bindings for players and global game actions.
 */
public final class KeyBindings {

    /**
     * Key bindings used for the player.
     */
    public static final PlayerKeyBindings PLAYER_KEY_BINDINGS = new PlayerKeyBindings(UP, LEFT, DOWN, RIGHT, SPACE, E);

    /**
     * Key to advance to the next dialog.
     */
    public static final int NEXT_DIALOG = ENTER;

    /**
     * Key to reset the game.
     */
    public static final int RESET_GAME = R;

    /**
     * Key to pause the game.
     */
    public static final int PAUSE_GAME = P;

    /**
     * Key to teleport to the BOSS room.
     */
    public static final int BOSS_ROOM = B;

    /**
     * Private constructor to prevent instantiation
     */
    private KeyBindings() {
    }

    /**
     * Key bindings record for a player
     *
     * @param up       (int): Key for moving up
     * @param left     (int): Key for moving left
     * @param down     (int): Key for moving down
     * @param right    (int): Key for moving right
     * @param pickaxe  (int): Key for using the pickaxe
     * @param interact (int): Key for remote interaction
     */
    public record PlayerKeyBindings(int up, int left, int down, int right, int pickaxe, int interact) {
    }
}
