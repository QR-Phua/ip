package alphaone.exception;

import alphaone.ui.Ui;

/**
 * Indicates that a referenced task id does not exist in the task list.
 */
public class InvalidTaskItemException extends Exception {
    /**
     * Creates the exception with a user-friendly message.
     */
    public InvalidTaskItemException() {
        super(Ui.BORDER + "\n" + "Invalid Task! Please try again." + "\n" + Ui.BORDER);

    }
}
