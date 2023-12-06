package model;

import java.time.LocalDateTime;

public class Appointments {

    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int customerId;
    private int contactId;
    private int userId;

    public Appointments() {
        // Default constructor
    }

    public Appointments(int id, String title, String description, String location, String type,
                        LocalDateTime startDateTime, LocalDateTime endDateTime, int customerId,
                        int contactId, int userId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStartDateTime(startDateTime);
        setEndDateTime(endDateTime);
        setCustomerId(customerId);
        setContactId(contactId);
        setUserId(userId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative.");
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null.");
        }
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty.");
        }
        this.type = type;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date and time cannot be null.");
        }
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        if (endDateTime == null) {
            throw new IllegalArgumentException("End date and time cannot be null.");
        }
        if (endDateTime.isBefore(startDateTime)) {
            throw new IllegalArgumentException("End date and time cannot be before the start date and time.");
        }
        this.endDateTime = endDateTime;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId < 0) {
            throw new IllegalArgumentException("Customer ID cannot be negative.");
        }
        this.customerId = customerId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        if (contactId < 0) {
            throw new IllegalArgumentException("Contact ID cannot be negative.");
        }
        this.contactId = contactId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative.");
        }
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Appointments{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", customerId=" + customerId +
                ", contactId=" + contactId +
                ", userId=" + userId +
                '}';
    }
}
