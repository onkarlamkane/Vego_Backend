package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the enquiries database table.
 * 
 */
@Entity
@Table(name="enquiries")
@NamedQuery(name="Enquiry.findAll", query="SELECT e FROM Enquiry e")
public class Enquiry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_active")
	private int isActive;

	@Lob
	private String message;

	@Column(name="mobile_number")
	private String mobileNumber;

	private String name;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public Enquiry() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getIsActive() {
		return this.isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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