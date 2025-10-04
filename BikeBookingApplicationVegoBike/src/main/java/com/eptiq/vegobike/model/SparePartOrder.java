package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the spare_part_orders database table.
 * 
 */
@Entity
@Table(name="spare_part_orders")
@NamedQuery(name="SparePartOrder.findAll", query="SELECT s FROM SparePartOrder s")
public class SparePartOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="order_amount")
	private double orderAmount;

	@Column(name="order_status")
	private int orderStatus;

	@Column(name="payment_method")
	private String paymentMethod;

	@Column(name="payment_status")
	private String paymentStatus;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public SparePartOrder() {
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

	public double getOrderAmount() {
		return this.orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public int getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}