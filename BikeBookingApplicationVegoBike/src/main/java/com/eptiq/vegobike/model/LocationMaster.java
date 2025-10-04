package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the location_masters database table.
 * 
 */
@Entity
@Table(name="location_masters")
@NamedQuery(name="LocationMaster.findAll", query="SELECT l FROM LocationMaster l")
public class LocationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Lob
	private String address;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_active")
	private int isActive;

	private String latitude;

	private String longitude;

	private int type;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public LocationMaster() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
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