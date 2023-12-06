package model;

/**
 * Represents a user with attributes including ID, name, and password.
 */
public class User {

    private int id;
    private String name;
    private String password;

    /**
     * Constructs a User instance.
     *
     * @param id       The unique identifier of the user.
     * @param name     The name of the user.
     * @param password The password for the user.
     */
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Retrieves the user's unique identifier.
     *
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets or updates the user's unique identifier.
     *
     * @param id The new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets or updates the user's name.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets or updates the user's password.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
