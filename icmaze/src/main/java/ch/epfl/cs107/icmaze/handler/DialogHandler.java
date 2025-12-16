package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * DialogHandler
 * Interface for handling dialog publication
 */
public interface DialogHandler {

    /**
     * Publish a dialog
     * 
     * @param dialog (Dialog): The dialog to publish, not null
     */
    void publish(Dialog dialog);
}
