package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the booking_notifications database table.
 * 
 */
@Entity
@Table(name="booking_notifications")
@NamedQuery(name="BookingNotification.findAll", query="SELECT b FROM BookingNotification b")
public class BookingNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="booking_id")
	private int bookingId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	private String token;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Lob
	@Column(name="vehicle_number")
	private String vehicleNumber;

	public BookingNotification() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBookingId() {
		return this.bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

}