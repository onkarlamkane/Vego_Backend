package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the service_order_items database table.
 * 
 */
@Entity
@Table(name="service_order_items")
@NamedQuery(name="ServiceOrderItem.findAll", query="SELECT s FROM ServiceOrderItem s")
public class ServiceOrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private int amount;

	@Column(name="brand_id")
	private int brandId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="discount_type")
	private int discountType;

	@Lob
	@Column(name="final_price")
	private String finalPrice;

	@Column(name="model_id")
	private int modelId;

	@Column(name="order_id")
	private int orderId;

	private int percent;

	private int quantity;

	@Column(name="service_id")
	private int serviceId;

	@Lob
	@Column(name="service_name")
	private String serviceName;

	@Lob
	@Column(name="service_price")
	private String servicePrice;

	@Lob
	@Column(name="service_type")
	private String serviceType;

	private int status;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public ServiceOrderItem() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public int getDiscountType() {
		return this.discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public String getFinalPrice() {
		return this.finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}

	public int getModelId() {
		return this.modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getOrderId() {
		return this.orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getPercent() {
		return this.percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
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

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServicePrice() {
		return this.servicePrice;
	}

	public void setServicePrice(String servicePrice) {
		this.servicePrice = servicePrice;
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

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}