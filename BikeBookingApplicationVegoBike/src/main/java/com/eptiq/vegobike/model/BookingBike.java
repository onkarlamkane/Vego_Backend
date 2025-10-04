package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the booking_bikes database table.
 */
@Entity
@Table(name="booking_bikes")
@NamedQuery(name="BookingBike.findAll", query="SELECT b FROM BookingBike b")
public class BookingBike implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Add this if id is auto-increment
	private int id;

	@Column(name="booking_id")
	private int bookingId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	private String images;

	private int type;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BookingBike() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

	public String getImages() {
		return this.images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}
