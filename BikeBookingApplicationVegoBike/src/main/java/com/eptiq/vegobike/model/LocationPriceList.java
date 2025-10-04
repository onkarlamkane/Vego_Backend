package com.eptiq.vegobike.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the location_price_lists database table.
 * 
 */
@Entity
@Table(name="location_price_lists")
@NamedQuery(name="LocationPriceList.findAll", query="SELECT l FROM LocationPriceList l")
public class LocationPriceList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_active")
	private int isActive;

	@Column(name="is_default")
	private int isDefault;

	private int km;

	private int price;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public LocationPriceList() {
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

	public int getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public int getKm() {
		return this.km;
	}

	public void setKm(int km) {
		this.km = km;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}