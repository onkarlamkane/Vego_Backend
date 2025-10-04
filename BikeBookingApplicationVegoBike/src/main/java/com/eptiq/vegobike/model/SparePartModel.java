package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the spare_part_models database table.
 * 
 */
@Entity
@Table(name="spare_part_models")
@NamedQuery(name="SparePartModel.findAll", query="SELECT s FROM SparePartModel s")
public class SparePartModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="brand_id")
	private int brandId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="model_id")
	private int modelId;

	@Column(name="spare_part_id")
	private int sparePartId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public SparePartModel() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBrandId() {
		return this.brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getModelId() {
		return this.modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getSparePartId() {
		return this.sparePartId;
	}

	public void setSparePartId(int sparePartId) {
		this.sparePartId = sparePartId;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}