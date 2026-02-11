package alphaone.model;

import java.util.ArrayList;

/**
 * Simple data holder for a contact's name and phone number.
 */
public class Contact {
    private String name;
    private String phone;

    /**
     * Create a Contact from an arguments list containing name then phone.
     *
     * @param arguments an ArrayList where index 0 is the name and index 1 is the phone
     */
    public Contact(ArrayList<String> arguments) {
        this.name = arguments.get(0);
        this.phone = arguments.get(1);
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
        return String.format("%s!@!%s", name, phone);
    }
}
