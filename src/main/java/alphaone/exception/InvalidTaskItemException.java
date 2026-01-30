package alphaone.exception;

/**
 * Indicates that a referenced task id does not exist in the task list.
 */
public class InvalidTaskItemException extends Exception {
    /**
     * Creates the exception with a user-friendly message.
     */
    public InvalidTaskItemException() {
        super("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Invalid Task! Please try again.
                +––––––––––––––––––––––––––––––––––––––––––––––+""");

    }
}
