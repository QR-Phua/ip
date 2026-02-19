package alphaone.model;

import alphaone.util.Constants;

/**
 * Simple data holder for a contact's name and phone number.
 */
public class Contact {
    private final String name;
    private final String phone;

    /**
     * Creates a Contact with the given name and phone number.
     *
     * @param name  the contact's full name
     * @param phone the contact's phone number
     */
    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
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
