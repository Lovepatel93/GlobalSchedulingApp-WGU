package model;

/**
 * Represents a Countries entity with attributes for its ID and name.
 */
public class Countries {

    private int id;
    private String name;

    /**
     * Constructor to initialize a Countries object.
     *
     * @param id The unique identifier for the country.
     * @param name The name of the country.
     */
    public Countries(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the ID of the country.
     *
     * @return int representing the country's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the country.
     *
     * @return String representing the country's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Provides a string representation of the country, which is its name.
     *
     * @return String representation of the country.
     */
    @Override
    public String toString() {
        return name;
    }
}
