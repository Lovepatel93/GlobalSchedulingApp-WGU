package model;

/**
 * Represents a summary of appointments based on their type, encapsulating the appointment type and the total count.
 */
public class TypeReport {

    private String type;
    private int count;

    /**
     * Constructs a TypeReport instance.
     *
     * @param type  The type of appointment for which the report is generated.
     * @param count The total number of appointments of the specified type.
     */
    public TypeReport(String type, int count) {
        this.type = type;
        this.count = count;
    }

    /**
     * Retrieves the type of appointment for which the report is generated.
     *
     * @return The appointment type as a string.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the total number of appointments of the specified type.
     *
     * @return The total number of appointments.
     */
    public int getCount() {
        return count;
    }
}
