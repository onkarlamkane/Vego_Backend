package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the brand_categories database table.
 * 
 */
@Entity
@Table(name="brand_categories")
@NamedQuery(name="BrandCategory.findAll", query="SELECT b FROM BrandCategory b")
public class BrandCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="brand_id")
	private int brandId;

	@Column(name="category_id")
	private int categoryId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_active")
	private int isActive;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BrandCategory() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBrandId() {
		return this.brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
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

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}