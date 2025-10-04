package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the user_notifications database table.
 * 
 */
@Entity
@Table(name="user_notifications")
@NamedQuery(name="UserNotification.findAll", query="SELECT u FROM UserNotification u")
public class UserNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	private String description;

	@Lob
	private String image;

	@Column(name="is_sent")
	private int isSent;

	private String page;

	@Lob
	private String title;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Column(name="user_id")
	private int userId;

	@Column(name="vehicle_number")
	private String vehicleNumber;

	public UserNotification() {
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getIsSent() {
		return this.isSent;
	}

	public void setIsSent(int isSent) {
		this.isSent = isSent;
	}

	public String getPage() {
		return this.page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

}