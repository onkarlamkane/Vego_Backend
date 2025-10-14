package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the offers database table.
 * 
 */
@Entity
@Table(name="offers")
@NamedQuery(name="Offer.findAll", query="SELECT o FROM Offer o")
public class Offer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="applies_to")
	private String appliesTo;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	@Column(name="customer_ids")
	private String customerIds;

	@Column(name="discount_type")
	private String discountType;

	@Column(name="discount_value")
	private Integer discountValue;

	private String eligibility;

	@Column(name="end_date")
	private Timestamp endDate;

	@Column(name="is_active")
	private Integer isActive;

	private Integer limit1;

	private Integer limit2;

	@Column(name="minimum_amount")
	private Integer minimumAmount;

	@Column(name="offer_code")
	private String offerCode;

	@Lob
	@Column(name="offer_image")
	private String offerImage;

	@Lob
	@Column(name="offer_name")
	private String offerName;

	@Column(name="remaining_coupon")
	private Integer remainingCoupon;

	@Column(name="start_date")
	private Timestamp startDate;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Column(name="usage_limit")
	private Integer usageLimit;

	public Offer() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppliesTo() {
		return this.appliesTo;
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getCustomerIds() {
		return this.customerIds;
	}

	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
	}

	public String getDiscountType() {
		return this.discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public Integer getDiscountValue() {
		return this.discountValue;
	}

	public void setDiscountValue(Integer discountValue) {
		this.discountValue = discountValue;
	}

	public String getEligibility() {
		return this.eligibility;
	}

	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Integer getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getLimit1() {
		return this.limit1;
	}

	public void setLimit1(Integer limit1) {
		this.limit1 = limit1;
	}

	public Integer getLimit2() {
		return this.limit2;
	}

	public void setLimit2(Integer limit2) {
		this.limit2 = limit2;
	}

	public Integer getMinimumAmount() {
		return this.minimumAmount;
	}

	public void setMinimumAmount(Integer minimumAmount) {
		this.minimumAmount = minimumAmount;
	}

	public String getOfferCode() {
		return this.offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getOfferImage() {
		return this.offerImage;
	}

	public void setOfferImage(String offerImage) {
		this.offerImage = offerImage;
	}

	public String getOfferName() {
		return this.offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public Integer getRemainingCoupon() {
		return this.remainingCoupon;
	}

	public void setRemainingCoupon(Integer remainingCoupon) {
		this.remainingCoupon = remainingCoupon;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getUsageLimit() {
		return this.usageLimit;
	}

	public void setUsageLimit(Integer usageLimit) {
		this.usageLimit = usageLimit;
	}

}