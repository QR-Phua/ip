package alphaone.model;

import alphaone.exception.InvalidContactException;
import alphaone.util.Constants;

/**
 * Simple data holder for a contact's name and phone number.
 *
 * <p>Use {@link #of(String, String)} to create validated instances.
 * The package-private constructor is reserved for storage deserialisation
 * where the data is already assumed to be valid.
 */
public class Contact {
    private final String name;
    private final String phone;

    /**
     * Package-private constructor used by storage deserialisation.
     * Does not validate inputs — assumes data read from disk is already valid.
     *
     * @param name  the contact's full name
     * @param phone the contact's phone number
     */
    Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    /**
     * Creates a validated Contact.
     *
     * @param name  the contact's full name (must be non-blank)
     * @param phone the contact's phone number (must be non-blank and digits only)
     * @return a new Contact instance
     * @throws InvalidContactException if the name is blank, the phone is blank,
     *                          or the phone contains non-digit characters
     */
    public static Contact of(String name, String phone) throws InvalidContactException {
        String trimmedName = name == null ? "" : name.trim();
        String trimmedPhone = phone == null ? "" : phone.trim();

        if (trimmedName.isEmpty()) {
            throw new InvalidContactException(InvalidContactException.Reason.EMPTY_NAME);
        }
        if (trimmedPhone.isEmpty()) {
            throw new InvalidContactException(InvalidContactException.Reason.EMPTY_PHONE);
        }
        if (!trimmedPhone.matches("\\d+")) {
            throw new InvalidContactException(InvalidContactException.Reason.INVALID_PHONE, trimmedPhone);
        }
        return new Contact(trimmedName, trimmedPhone);
    }

    /**
     * Reconstructs a Contact directly from stored data without validation.
     * For use by the storage layer only — data is assumed to have been validated on the way in.
     *
     * @param name  stored name
     * @param phone stored phone
     * @return a Contact instance
     */
    public static Contact fromStorage(String name, String phone) {
        return new Contact(name, phone);
    }

    /**
     * Returns the contact's name.
     *
     * @return contact name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the contact's phone number.
     *
     * @return phone number string
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Serialise the contact into a single-line representation for storage.
     *
     * @return serialised contact string
     */
    public String serialiseContact() {
        return name + Constants.STORAGE_SEPARATOR + phone;
    }
}
