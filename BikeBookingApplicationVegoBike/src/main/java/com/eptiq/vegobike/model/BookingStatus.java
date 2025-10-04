package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the booking_statuses database table.
 * 
 */
@Entity
@Table(name="booking_statuses")
@NamedQuery(name="BookingStatus.findAll", query="SELECT b FROM BookingStatus b")
public class BookingStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="color_code")
	private String colorCode;

	@Column(name="created_at")
	private Timestamp createdAt;

	private String name;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BookingStatus() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getColorCode() {
		return this.colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}