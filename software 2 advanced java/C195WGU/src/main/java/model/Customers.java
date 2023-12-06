package model;

/**
 * Represents a single customer entity with attributes to identify and describe a customer.
 * This class provides setters and getters for encapsulated data manipulation.
 */
public class Customers {

    private int customerId;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int divisionId;
    private String divisionName;

    /**
     * Constructs a Customers object.
     *
     * @param customerId      The unique identifier for the customer.
     * @param name            The name of the customer.
     * @param address         The address of the customer.
     * @param postalCode      The postal code of the customer's address.
     * @param phoneNumber     The customer's phone number.
     * @param divisionId      The unique identifier for the division.
     * @param divisionName    The name of the division.
     */
    public Customers(int customerId, String name, String address, String postalCode,
                     String phoneNumber, int divisionId, String divisionName) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionId = divisionId;
        this.divisionName = divisionName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

}
