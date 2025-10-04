package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notification_msg") // ✅ changed table name
public class NotificationMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient; // ADMIN / STORE_MANAGER / CUSTOMER
    private String message;
    private String type; // BOOKING_CREATED, STATUS_UPDATED, etc.

    @Column(name="booking_id")
    private int bookingId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    private boolean read;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
