package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the service_orders database table.
 * 
 */
@Entity
@Table(name="service_orders")
@NamedQuery(name="ServiceOrder.findAll", query="SELECT s FROM ServiceOrder s")
public class ServiceOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private int amount;

	@Column(name="chasis_number")
	private String chasisNumber;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name="discount_type")
	private int discountType;

	@Lob
	@Column(name="engine_number")
	private String engineNumber;

	@Lob
	@Column(name="final_amount")
	private String finalAmount;

	@Column(name="kms_driven")
	private String kmsDriven;

	@Lob
	@Column(name="next_service_date")
	private String nextServiceDate;

	@Column(name="order_amount")
	private double orderAmount;

	@Lob
	@Column(name="order_id")
	private String orderId;

	@Column(name="order_status")
	private int orderStatus;

	@Column(name="payment_method")
	private String paymentMethod;

	@Column(name="payment_status")
	private String paymentStatus;

	private int percent;

	@Lob
	@Column(name="service_comments")
	private String serviceComments;

	@Lob
	@Column(name="slot_time")
	private String slotTime;

	@Column(name="store_id")
	private int storeId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Column(name="vehicle_number")
	private String vehicleNumber;

	public ServiceOrder() {
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

	public String getChasisNumber() {
		return this.chasisNumber;
	}

	public void setChasisNumber(String chasisNumber) {
		this.chasisNumber = chasisNumber;
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

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDiscountType() {
		return this.discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public String getEngineNumber() {
		return this.engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getFinalAmount() {
		return this.finalAmount;
	}

	public void setFinalAmount(String finalAmount) {
		this.finalAmount = finalAmount;
	}

	public String getKmsDriven() {
		return this.kmsDriven;
	}

	public void setKmsDriven(String kmsDriven) {
		this.kmsDriven = kmsDriven;
	}

	public String getNextServiceDate() {
		return this.nextServiceDate;
	}

	public void setNextServiceDate(String nextServiceDate) {
		this.nextServiceDate = nextServiceDate;
	}

	public double getOrderAmount() {
		return this.orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public int getPercent() {
		return this.percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public String getServiceComments() {
		return this.serviceComments;
	}

	public void setServiceComments(String serviceComments) {
		this.serviceComments = serviceComments;
	}

	public String getSlotTime() {
		return this.slotTime;
	}

	public void setSlotTime(String slotTime) {
		this.slotTime = slotTime;
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

	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

}