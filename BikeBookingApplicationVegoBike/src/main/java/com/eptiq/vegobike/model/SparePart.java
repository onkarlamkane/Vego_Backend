package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the spare_parts database table.
 * 
 */
@Entity
@Table(name="spare_parts")
@NamedQuery(name="SparePart.findAll", query="SELECT s FROM SparePart s")
public class SparePart implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="deleted_at")
	private Timestamp deletedAt;

	@Column(name="is_type")
	private int isType;

	@Lob
	@Column(name="part_description")
	private String partDescription;

	@Lob
	@Column(name="part_image")
	private String partImage;

	@Column(name="part_name")
	private String partName;

	private int price;

	private int status;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Column(name="year_id")
	private int yearId;

	public SparePart() {
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

	public Timestamp getDeletedAt() {
		return this.deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public int getIsType() {
		return this.isType;
	}

	public void setIsType(int isType) {
		this.isType = isType;
	}

	public String getPartDescription() {
		return this.partDescription;
	}

	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	public String getPartImage() {
		return this.partImage;
	}

	public void setPartImage(String partImage) {
		this.partImage = partImage;
	}

	public String getPartName() {
		return this.partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
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

	public int getYearId() {
		return this.yearId;
	}

	public void setYearId(int yearId) {
		this.yearId = yearId;
	}

}