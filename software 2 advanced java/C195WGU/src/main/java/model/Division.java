package model;

public class Division {
    private int id;  // Keeping the same as before
    private String name;  // Keeping the same as before
    private int countryId;  // Modified from country_ID for better naming convention

    /**
     * Constructor to initialize the Division object.
     *
     * @param id The unique identifier of the division.
     * @param name The name of the division.
     * @param countryId The unique identifier of the country to which the division belongs.
     */
    public Division(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    /**
     * Retrieves the ID of the division.
     *
     * @return int representing the ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the division.
     *
     * @return String representing the division name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the country ID associated with the division.
     *
     * @return int representing the country ID.
     */
    public int getCountryId() {
        return countryId;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
