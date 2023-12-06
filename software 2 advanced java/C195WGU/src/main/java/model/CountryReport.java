package model;

/**
 * Represents a summary report for countries, encapsulating the country name and the total count associated with it.
 */
public class CountryReport {

    private String name;
    private int total;

    /**
     * Constructs a CountryReport instance.
     *
     * @param name  The name of the country for which the report is generated.
     * @param total The total count or value associated with the country.
     */
    public CountryReport(String name, int total) {
        this.name = name;
        this.total = total;
    }

    /**
     * Retrieves the name of the country for which the report is generated.
     *
     * @return The country name as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the total count or value associated with the country.
     *
     * @return The total value.
     */
    public int getTotal() {
        return total;
    }
}
