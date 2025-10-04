package com.eptiq.vegobike.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the bike_sale_enquiries database table.
 *
 */
@Entity
@Table(name="bike_sale_enquiries")
@NamedQuery(name="BikeSaleEnquiry.findAll", query="SELECT b FROM BikeSaleEnquiry b")
public class BikeSaleEnquiry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="bike_id")
	private int bikeId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Lob
	@Column(name="enquiry_id")
	private String enquiryId;

	private String status;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BikeSaleEnquiry() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBikeId() {
		return this.bikeId;
	}

	public void setBikeId(int bikeId) {
		this.bikeId = bikeId;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getEnquiryId() {
		return this.enquiryId;
	}

	public void setEnquiryId(String enquiryId) {
		this.enquiryId = enquiryId;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
