package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the spare_carts database table.
 * 
 */
@Entity
@Table(name="spare_carts")
@NamedQuery(name="SpareCart.findAll", query="SELECT s FROM SpareCart s")
public class SpareCart implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="part_id")
	private int partId;

	private int price;

	private int quantity;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public SpareCart() {
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

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}