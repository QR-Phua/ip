package alphaone.model;

import java.util.ArrayList;

import alphaone.storage.Storage;

/**
 * Container for contact records used by the application.
 */
public class ContactList {
    private final ArrayList<Contact> contacts;
    private final Storage storage; // may be null for in-memory-only

    /**
     * Create a ContactList backed by the provided storage.
     *
     * If {@code storage} is non-null this constructor attempts to load saved contacts
     * from the storage; otherwise an empty in-memory list is created.
     *
     * @param storage storage backend used to persist contacts; may be null for in-memory-only
     */
    public ContactList(Storage storage) {
        this.storage = storage;
        if (this.storage != null) {
            ArrayList<Contact> loaded = this.storage.loadContacts();
            if (loaded != null) {
                this.contacts = loaded;
                return;
            }
        }
        this.contacts = new ArrayList<>();
    }

    /**
     * Add a contact to the list and persist if storage is available.
     *
     * @param contact the Contact to add
     */
    public void addContact(Contact contact) {
        this.contacts.add(contact);
        if (this.storage != null) {
            this.storage.saveContacts(this.contacts);
        }
    }

    /**
     * Remove the first contact matching the provided name and persist the change.
     *
     * @param name the name to search for
     * @return the removed Contact or null if none matched
     */
    public Contact removeContactByName(String name) {
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.getName().equalsIgnoreCase(name)) {
                contacts.remove(i);
                if (this.storage != null) {
                    this.storage.saveContacts(this.contacts);
                }
                return contact;
            }
        }
        return null;
    }

    /**
     * Returns a formatted string containing saved contacts for display.
     *
     * @return formatted contacts text, or a message indicating no contacts are saved.
     */
    public String formatContactsDisplay() {
        StringBuilder result = new StringBuilder();
        if (contacts.isEmpty()) {
            result.append("You have no saved contacts.\n");
        } else {
            result.append("Saved contacts:\n");
            for (Contact contact : contacts) {
                result.append(String.format("%s (%s)\n", contact.getName(), contact.getPhone()));
            }
        }
        return result.toString();
    }

}
