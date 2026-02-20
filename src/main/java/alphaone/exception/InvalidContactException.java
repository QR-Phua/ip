package alphaone.exception;

/**
 * Signals a contact-specific error such as a duplicate name, missing name/phone,
 * an invalid phone number, or an attempt to remove a contact that does not exist.
 */
public class InvalidContactException extends Exception {

    /** Distinguishes the kind of contact error so callers can produce targeted messages. */
    public enum Reason {
        DUPLICATE_CONTACT,
        NOT_FOUND,
        EMPTY_NAME,
        EMPTY_PHONE,
        INVALID_PHONE
    }

    private final Reason reason;
    private final String detail;

    /**
     * Creates an InvalidContactException with the given reason.
     * Use this constructor when no specific detail needs to be embedded in the message.
     *
     * @param reason the reason for the failure
     */
    public InvalidContactException(Reason reason) {
        this(reason, null);
    }

    /**
     * Creates an InvalidContactException with the given reason and a detail string
     * (e.g. the offending name or phone value) to embed in the message.
     *
     * @param reason the reason for the failure
     * @param detail the value that triggered the error (may be null)
     */
    public InvalidContactException(Reason reason, String detail) {
        super();
        this.reason = reason;
        this.detail = detail;
    }

    @Override
    public String getMessage() {
        switch (reason) {
        case DUPLICATE_CONTACT -> {
            return "A contact with the name and phone number \"" + detail + "\" already exists.\n"
                    + "Use 'contact list' to see all saved contacts.";
        }
        case NOT_FOUND -> {
            return "No contact found with the name \"" + detail + "\".\n"
                    + "Use 'contact list' to see all saved contacts.";
        }
        case EMPTY_NAME -> {
            return "Contact name cannot be empty.\n"
                    + "Examples:\n"
                    + "  contact add John Doe 91234567\n"
                    + "  contact remove John Doe";
        }
        case EMPTY_PHONE -> {
            return "Phone number cannot be empty.\n"
                    + "Example: contact add John Doe 91234567";
        }
        case INVALID_PHONE -> {
            return "\"" + detail + "\" is not a valid phone number.\n"
                    + "Phone numbers must contain digits only (e.g., 91234567).";
        }
        default -> {
            return "Contact operation failed. Please try again.";
        }
        }
    }
}

