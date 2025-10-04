package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the bike_seller_details database table.
 * 
 */
@Entity
@Table(name="bike_seller_details")
@NamedQuery(name="BikeSellerDetail.findAll", query="SELECT b FROM BikeSellerDetail b")
public class BikeSellerDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Lob
	private String address;

	@Lob
	@Column(name="alternate_contact_number")
	private String alternateContactNumber;

	@Column(name="bike_id")
	private int bikeId;

	@Lob
	private String city;

	@Lob
	@Column(name="contact_number")
	private String contactNumber;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	private String email;

	@Lob
	private String name;

	@Lob
	private String pincode;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BikeSellerDetail() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlternateContactNumber() {
		return this.alternateContactNumber;
	}

	public void setAlternateContactNumber(String alternateContactNumber) {
		this.alternateContactNumber = alternateContactNumber;
	}

	public int getBikeId() {
		return this.bikeId;
	}

	public void setBikeId(int bikeId) {
		this.bikeId = bikeId;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPincode() {
		return this.pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}