package com.eptiq.vegobike.model;

import jakarta.persistence.*;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the bikes database table.
 * 
 */
@Entity
@Table(name="bikes")
@NamedQuery(name="Bike.findAll", query="SELECT b FROM Bike b")
public class Bike implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="brand_id")
	private int brandId;

	@Column(name="category_id")
	private int categoryId;

	@Lob
	@Column(name="chassis_number")
	private String chassisNumber;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="dealer_id")
	private int dealerId;

	@Lob
	@Column(name="default_address")
	private String defaultAddress;

	@Lob
	@Column(name="document_image")
	private String documentImage;

	@Lob
	@Column(name="engine_number")
	private String engineNumber;

	@Column(name="engine_status")
	private int engineStatus;

	@Column(name="imei_number")
	private String imeiNumber;

	@Lob
	@Column(name="insurance_image")
	private String insuranceImage;

	@Column(name="is_active")
	private int isActive;

	@Column(name="is_documents")
	private int isDocuments;

	@Column(name="is_insurance")
	private int isInsurance;

	@Column(name="is_puc")
	private int isPuc;

	@Lob
	private String latitude;

	@Lob
	private String longitude;

	@Column(name="model_id")
	private int modelId;

	private String name;

	private int price;

	@Lob
	@Column(name="puc_image")
	private String pucImage;

	@Column(name="registration_number")
	private String registrationNumber;

	@Column(name="registration_year_id")
	private int registrationYearId;

	@Column(name="store_id")
	private int storeId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@OneToMany(mappedBy = "bike", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<BikeImage> bikeImages = new ArrayList<>();


	public Bike() {
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

	public String getChassisNumber() {
		return this.chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getDealerId() {
		return this.dealerId;
	}

	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}

	public String getDefaultAddress() {
		return this.defaultAddress;
	}

	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public String getDocumentImage() {
		return this.documentImage;
	}

	public void setDocumentImage(String documentImage) {
		this.documentImage = documentImage;
	}

	public String getEngineNumber() {
		return this.engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public int getEngineStatus() {
		return this.engineStatus;
	}

	public void setEngineStatus(int engineStatus) {
		this.engineStatus = engineStatus;
	}

	public String getImeiNumber() {
		return this.imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getInsuranceImage() {
		return this.insuranceImage;
	}

	public void setInsuranceImage(String insuranceImage) {
		this.insuranceImage = insuranceImage;
	}

	public int getIsActive() {
		return this.isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getIsDocuments() {
		return this.isDocuments;
	}

	public void setIsDocuments(int isDocuments) {
		this.isDocuments = isDocuments;
	}

	public int getIsInsurance() {
		return this.isInsurance;
	}

	public void setIsInsurance(int isInsurance) {
		this.isInsurance = isInsurance;
	}

	public int getIsPuc() {
		return this.isPuc;
	}

	public void setIsPuc(int isPuc) {
		this.isPuc = isPuc;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public int getModelId() {
		return this.modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getPucImage() {
		return this.pucImage;
	}

	public void setPucImage(String pucImage) {
		this.pucImage = pucImage;
	}

	public String getRegistrationNumber() {
		return this.registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public int getRegistrationYearId() {
		return this.registrationYearId;
	}

	public void setRegistrationYearId(int registrationYearId) {
		this.registrationYearId = registrationYearId;
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