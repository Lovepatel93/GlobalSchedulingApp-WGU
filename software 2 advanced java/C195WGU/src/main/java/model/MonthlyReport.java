package model;

/**
 * Represents a summary of monthly appointments, encapsulating the month and the total number of appointments.
 */
public class MonthlyReport {

    private String month;
    private int totalAppointments;

    /**
     * Constructs a MonthlyReport instance.
     *
     * @param month            The month for which the report is generated.
     * @param totalAppointments The total number of appointments in the specified month.
     */
    public MonthlyReport(String month, int totalAppointments) {
        this.month = month;
        this.totalAppointments = totalAppointments;
    }

    /**
     * Retrieves the month for which the report is generated.
     *
     * @return The month as a string.
     */
    public String getMonth() {
        return month;
    }

    /**
     * Retrieves the total number of appointments in the specified month.
     *
     * @return The total number of appointments.
     */
    public int getTotalAppointments() {
        return totalAppointments;
    }
}
