package model;

/**
 * Represents a Contact entity with attributes ID, name, and email.
 */
public class Contacts {

    private int contactId;
    private String contactName;
    private String contactEmail;

    /**
     * Constructor to initialize the Contact object.
     *
     * @param contactId The unique identifier of the contact.
     * @param contactName The name of the contact.
     * @param contactEmail The email of the contact.
     */
    public Contacts(int contactId, String contactName, String contactEmail) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * Retrieves the ID of the contact.
     *
     * @return int representing the contact's ID.
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Retrieves the name of the contact.
     *
     * @return String representing the contact's name.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Retrieves the email of the contact.
     *
     * @return String representing the contact's email.
     */
    public String getContactEmail() {
        return contactEmail;
    }

    @Override
    public String toString() {
        return this.contactName;
    }
}
