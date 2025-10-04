package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the spare_part_enquiries database table.
 * 
 */
@Entity
@Table(name="spare_part_enquiries")
@NamedQuery(name="SparePartEnquiry.findAll", query="SELECT s FROM SparePartEnquiry s")
public class SparePartEnquiry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="part_id")
	private int partId;

	private int quantity;

	private int status;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public SparePartEnquiry() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getPartId() {
		return this.partId;
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}