package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the user_otps database table.
 * 
 */
@Entity
@Table(name="user_otps")
@NamedQuery(name="UserOtp.findAll", query="SELECT u FROM UserOtp u")
public class UserOtp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="contact_number")
	private String contactNumber;

	@Column(name="created_at")
	private Timestamp createdAt;

	private int otp;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public UserOtp() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContactNumber() {
		return this.contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getOtp() {
		return this.otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}