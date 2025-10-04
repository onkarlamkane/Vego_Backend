package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the bike_service_enquiries database table.
 * 
 */
@Entity
@Table(name="bike_service_enquiries")
@NamedQuery(name="BikeServiceEnquiry.findAll", query="SELECT b FROM BikeServiceEnquiry b")
public class BikeServiceEnquiry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="brand_id")
	private int brandId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="model_id")
	private int modelId;

	private int quantity;

	@Column(name="service_id")
	private int serviceId;

	@Lob
	@Column(name="service_type")
	private String serviceType;

	private int status;

	@Column(name="store_id")
	private int storeId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BikeServiceEnquiry() {
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

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getModelId() {
		return this.modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getServiceId() {
		return this.serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStoreId() {
		return this.storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}