package alphaone.model;

import java.util.ArrayList;
import java.util.Iterator;

import alphaone.exception.InvalidContactException;
import alphaone.storage.Storage;
import alphaone.util.StringUtils;

/**
 * Container for contact records used by the application.
 */
public class ContactList {
    private final ArrayList<Contact> contacts;
    /** May be null for in-memory-only usage. */
    private final Storage storage;

    /**
     * Creates a ContactList backed by the provided storage.
     *
     * <p>If {@code storage} is non-null this constructor attempts to load saved contacts
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
     * Adds a contact to the list and persists if storage is available.
     *
     * @param contact the Contact to add
     * @throws InvalidContactException if a contact with the same name already exists
     */
    public void addContact(Contact contact) throws InvalidContactException {
        for (Contact existing : contacts) {
            if (existing.getName().equalsIgnoreCase(contact.getName())
                    && existing.getPhone().equals(contact.getPhone())) {
                String detail = contact.getName() + " (" + contact.getPhone() + ")";
                throw new InvalidContactException(InvalidContactException.Reason.DUPLICATE_CONTACT, detail);
            }
        }
        this.contacts.add(contact);
        if (this.storage != null) {
            this.storage.saveContacts(this.contacts);
        }
    }

    /**
     * Removes the first contact matching the provided name (case-insensitive) and persists the change.
     *
     * @param name the name to search for
     * @return the removed Contact
     * @throws InvalidContactException if no contact with that name exists
     */
    public Contact removeContactByName(String name) throws InvalidContactException {
        Iterator<Contact> iterator = contacts.iterator();
        while (iterator.hasNext()) {
            Contact contact = iterator.next();
            if (contact.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                if (this.storage != null) {
                    this.storage.saveContacts(this.contacts);
                }
                return contact;
            }
        }
        throw new InvalidContactException(InvalidContactException.Reason.NOT_FOUND, name);
    }

    /**
     * Returns a formatted string containing saved contacts for display.
     *
     * @return formatted contacts text, or a message indicating no contacts are saved.
     */
    public String formatContactsDisplay() {
        StringBuilder result = new StringBuilder();
        if (contacts.isEmpty()) {
            result.append("You have no saved contacts.");
        } else {
            result.append("Saved contacts:\n");
            for (Contact contact : contacts) {
                result.append(String.format("%s (%s)\n", contact.getName(), contact.getPhone()));
            }
            StringUtils.trimTrailingNewline(result);
        }
        return result.toString();
    }
}
