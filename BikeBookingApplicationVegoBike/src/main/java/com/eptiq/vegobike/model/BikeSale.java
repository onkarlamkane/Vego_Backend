//
//package com.eptiq.vegobike.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.sql.Timestamp;
//
//
///**
// * The persistent class for the bike_sales database table.
// *
// */
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name="bike_sales")
//@NamedQuery(name="BikeSale.findAll", query="SELECT b FROM BikeSale b")
//public class BikeSale implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	private long id;
//
//	@Column(name="added_by")
//	private int addedBy;
//
//	@Lob
//	@Column(name="additional_notes")
//	private String additionalNotes;
//
//	@Lob
//	@Column(name="bike_condition")
//	private String bikeCondition;
//
//	@Lob
//	@Column(name="bike_description")
//	private String bikeDescription;
//
//	@Column(name="brand_id")
//	private int brandId;
//
//	@Column(name="category_id")
//	private int categoryId;
//
//	@Lob
//	private String color;
//
//	@Column(name="created_at")
//	private Timestamp createdAt;
//
//	@Column(name="customer_selling_closing_price")
//	private double customerSellingClosingPrice;
//
//	@Column(name="deleted_at")
//	private Timestamp deletedAt;
//
//	@Lob
//	@Column(name="document_image")
//	private String documentImage;
//
//	@Lob
//	@Column(name="inspection_bike_condition")
//	private String inspectionBikeCondition;
//
//	@Lob
//	@Column(name="insurance_image")
//	private String insuranceImage;
//
//	@Column(name="is_document")
//	private int isDocument;
//
//	@Column(name="is_insurance")
//	private int isInsurance;
//
//	@Column(name="is_puc")
//	private int isPuc;
//
//	@Column(name="is_repair_required")
//	private int isRepairRequired;
//
//	@Column(name="kms_driven")
//	private int kmsDriven;
//
//	@Column(name="model_id")
//	private int modelId;
//
//	@Column(name="number_of_owner")
//	private int numberOfOwner;
//
//	private int price;
//
//	@Lob
//	@Column(name="puc_image")
//	private String pucImage;
//
//	@Lob
//	@Column(name="registration_number")
//	private String registrationNumber;
//
//	@Lob
//	@Column(name="sell_id")
//	private String sellId;
//
//	@Column(name="selling_closing_price")
//	private double sellingClosingPrice;
//
//	@Column(name="selling_price")
//	private double sellingPrice;
//
//	private String status;
//
//	@Column(name="store_id")
//	private int storeId;
//
//	@Lob
//	@Column(name="supervisor_name")
//	private String supervisorName;
//
//	@Column(name="updated_at")
//	private Timestamp updatedAt;
//
//	@Lob
//	@Column(name="vehicle_chassis_number")
//	private String vehicleChassisNumber;
//
//	@Lob
//	@Column(name="vehicle_engine_number")
//	private String vehicleEngineNumber;
//
//	@Column(name="year_id")
//	private int yearId;
//
//	public BikeSale() {
//	}
//
//	public long getId() {
//		return this.id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}
//
//	public int getAddedBy() {
//		return this.addedBy;
//	}
//
//	public void setAddedBy(int addedBy) {
//		this.addedBy = addedBy;
//	}
//
//	public String getAdditionalNotes() {
//		return this.additionalNotes;
//	}
//
//	public void setAdditionalNotes(String additionalNotes) {
//		this.additionalNotes = additionalNotes;
//	}
//
//	public String getBikeCondition() {
//		return this.bikeCondition;
//	}
//
//	public void setBikeCondition(String bikeCondition) {
//		this.bikeCondition = bikeCondition;
//	}
//
//	public String getBikeDescription() {
//		return this.bikeDescription;
//	}
//
//	public void setBikeDescription(String bikeDescription) {
//		this.bikeDescription = bikeDescription;
//	}
//
//	public int getBrandId() {
//		return this.brandId;
//	}
//
//	public void setBrandId(int brandId) {
//		this.brandId = brandId;
//	}
//
//	public int getCategoryId() {
//		return this.categoryId;
//	}
//
//	public void setCategoryId(int categoryId) {
//		this.categoryId = categoryId;
//	}
//
//	public String getColor() {
//		return this.color;
//	}
//
//	public void setColor(String color) {
//		this.color = color;
//	}
//
//	public Timestamp getCreatedAt() {
//		return this.createdAt;
//	}
//
//	public void setCreatedAt(Timestamp createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public double getCustomerSellingClosingPrice() {
//		return this.customerSellingClosingPrice;
//	}
//
//	public void setCustomerSellingClosingPrice(double customerSellingClosingPrice) {
//		this.customerSellingClosingPrice = customerSellingClosingPrice;
//	}
//
//	public Timestamp getDeletedAt() {
//		return this.deletedAt;
//	}
//
//	public void setDeletedAt(Timestamp deletedAt) {
//		this.deletedAt = deletedAt;
//	}
//
//	public String getDocumentImage() {
//		return this.documentImage;
//	}
//
//	public void setDocumentImage(String documentImage) {
//		this.documentImage = documentImage;
//	}
//
//	public String getInspectionBikeCondition() {
//		return this.inspectionBikeCondition;
//	}
//
//	public void setInspectionBikeCondition(String inspectionBikeCondition) {
//		this.inspectionBikeCondition = inspectionBikeCondition;
//	}
//
//	public String getInsuranceImage() {
//		return this.insuranceImage;
//	}
//
//	public void setInsuranceImage(String insuranceImage) {
//		this.insuranceImage = insuranceImage;
//	}
//
//	public int getIsDocument() {
//		return this.isDocument;
//	}
//
//	public void setIsDocument(int isDocument) {
//		this.isDocument = isDocument;
//	}
//
//	public int getIsInsurance() {
//		return this.isInsurance;
//	}
//
//	public void setIsInsurance(int isInsurance) {
//		this.isInsurance = isInsurance;
//	}
//
//	public int getIsPuc() {
//		return this.isPuc;
//	}
//
//	public void setIsPuc(int isPuc) {
//		this.isPuc = isPuc;
//	}
//
//	public int getIsRepairRequired() {
//		return this.isRepairRequired;
//	}
//
//	public void setIsRepairRequired(int isRepairRequired) {
//		this.isRepairRequired = isRepairRequired;
//	}
//
//	public int getKmsDriven() {
//		return this.kmsDriven;
//	}
//
//	public void setKmsDriven(int kmsDriven) {
//		this.kmsDriven = kmsDriven;
//	}
//
//	public int getModelId() {
//		return this.modelId;
//	}
//
//	public void setModelId(int modelId) {
//		this.modelId = modelId;
//	}
//
//	public int getNumberOfOwner() {
//		return this.numberOfOwner;
//	}
//
//	public void setNumberOfOwner(int numberOfOwner) {
//		this.numberOfOwner = numberOfOwner;
//	}
//
//	public int getPrice() {
//		return this.price;
//	}
//
//	public void setPrice(int price) {
//		this.price = price;
//	}
//
//	public String getPucImage() {
//		return this.pucImage;
//	}
//
//	public void setPucImage(String pucImage) {
//		this.pucImage = pucImage;
//	}
//
//	public String getRegistrationNumber() {
//		return this.registrationNumber;
//	}
//
//	public void setRegistrationNumber(String registrationNumber) {
//		this.registrationNumber = registrationNumber;
//	}
//
//	public String getSellId() {
//		return this.sellId;
//	}
//
//	public void setSellId(String sellId) {
//		this.sellId = sellId;
//	}
//
//	public double getSellingClosingPrice() {
//		return this.sellingClosingPrice;
//	}
//
//	public void setSellingClosingPrice(double sellingClosingPrice) {
//		this.sellingClosingPrice = sellingClosingPrice;
//	}
//
//	public double getSellingPrice() {
//		return this.sellingPrice;
//	}
//
//	public void setSellingPrice(double sellingPrice) {
//		this.sellingPrice = sellingPrice;
//	}
//
//	public String getStatus() {
//		return this.status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public int getStoreId() {
//		return this.storeId;
//	}
//
//	public void setStoreId(int storeId) {
//		this.storeId = storeId;
//	}
//
//	public String getSupervisorName() {
//		return this.supervisorName;
//	}
//
//	public void setSupervisorName(String supervisorName) {
//		this.supervisorName = supervisorName;
//	}
//
//	public Timestamp getUpdatedAt() {
//		return this.updatedAt;
//	}
//
//	public void setUpdatedAt(Timestamp updatedAt) {
//		this.updatedAt = updatedAt;
//	}
//
//	public String getVehicleChassisNumber() {
//		return this.vehicleChassisNumber;
//	}
//
//	public void setVehicleChassisNumber(String vehicleChassisNumber) {
//		this.vehicleChassisNumber = vehicleChassisNumber;
//	}
//
//	public String getVehicleEngineNumber() {
//		return this.vehicleEngineNumber;
//	}
//
//	public void setVehicleEngineNumber(String vehicleEngineNumber) {
//		this.vehicleEngineNumber = vehicleEngineNumber;
//	}
//
//	public int getYearId() {
//		return this.yearId;
//	}
//
//	public void setYearId(int yearId) {
//		this.yearId = yearId;
//	}
//
//}



package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Slf4j
@Entity
@Table(name = "bike_sales", indexes = {
		@Index(name = "idx_bike_sale_brand", columnList = "brand_id"),
		@Index(name = "idx_bike_sale_model", columnList = "model_id"),
		@Index(name = "idx_bike_sale_status", columnList = "status"),
		@Index(name = "idx_bike_sale_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeSale implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "added_by")
	private Integer addedBy;

	@Column(name = "additional_notes", length = 1000)
	private String additionalNotes;

	@Column(name = "bike_condition", length = 500)
	private String bikeCondition;

	@Column(name = "bike_description", length = 1000)
	private String bikeDescription;

	@Column(name = "brand_id", nullable = false)
	private Integer brandId;

	@Column(name = "category_id", nullable = false)
	private Integer categoryId;

	@Column(length = 50)
	private String color;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "customer_selling_closing_price", precision = 10, scale = 2)
	private BigDecimal customerSellingClosingPrice;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	@Column(name = "document_image", length = 500)
	private String documentImage;

	@Column(name = "inspection_bike_condition", length = 1000)
	private String inspectionBikeCondition;

	@Column(name = "insurance_image", length = 500)
	private String insuranceImage;

	@Column(name = "is_document", nullable = false)
	@Builder.Default
	private Integer isDocument = 0;

	@Column(name = "is_insurance", nullable = false)
	@Builder.Default
	private Integer isInsurance = 0;

	@Column(name = "is_puc", nullable = false)
	@Builder.Default
	private Integer isPuc = 0;

	@Column(name = "is_repair_required", nullable = false)
	@Builder.Default
	private Integer isRepairRequired = 0;

	@Column(name = "kms_driven")
	private Integer kmsDriven;

	@Column(name = "model_id", nullable = false)
	private Integer modelId;

	@Column(name = "number_of_owner")
	private Integer numberOfOwner;

	@Column(precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "puc_image", length = 500)
	private String pucImage;

	@Column(name = "registration_number", length = 20)
	private String registrationNumber;

	@Column(name = "sell_id", length = 50)
	private String sellId;

	@Column(name = "selling_closing_price", precision = 10, scale = 2)
	private BigDecimal sellingClosingPrice;

	@Column(name = "selling_price", precision = 10, scale = 2)
	private BigDecimal sellingPrice;

	@Column(length = 50)
	@Builder.Default
	private String status = "PENDING";

	@Column(name = "store_id")
	private Integer storeId;

	@Column(name = "supervisor_name", length = 100)
	private String supervisorName;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

	@Column(name = "vehicle_chassis_number", length = 50)
	private String vehicleChassisNumber;

	@Column(name = "vehicle_engine_number", length = 50)
	private String vehicleEngineNumber;

	@Column(name = "year_id")
	private Integer yearId;

	// ✅ Helper methods for business logic
	public boolean hasDocuments() {
		return isDocument != null && isDocument == 1;
	}

	public boolean hasInsurance() {
		return isInsurance != null && isInsurance == 1;
	}

	public boolean hasPuc() {
		return isPuc != null && isPuc == 1;
	}

	public boolean requiresRepair() {
		return isRepairRequired != null && isRepairRequired == 1;
	}

	public boolean isActive() {
		return "ACTIVE".equalsIgnoreCase(status);
	}

	public boolean isPending() {
		return "PENDING".equalsIgnoreCase(status);
	}

	public boolean isSold() {
		return "SOLD".equalsIgnoreCase(status);
	}

	public void markAsActive() {
		this.status = "ACTIVE";
	}

	public void markAsSold() {
		this.status = "SOLD";
	}

	public void markAsInactive() {
		this.status = "INACTIVE";
	}

	// ✅ Price calculation helpers
	public BigDecimal getFinalSellingPrice() {
		return sellingClosingPrice != null ? sellingClosingPrice : sellingPrice;
	}

	public BigDecimal getPriceAdjustment() {
		if (sellingClosingPrice != null && sellingPrice != null) {
			return sellingClosingPrice.subtract(sellingPrice);
		}
		return BigDecimal.ZERO;
	}

	@PrePersist
	protected void onCreate() {
		if (status == null) status = "PENDING";
		if (isDocument == null) isDocument = 0;
		if (isInsurance == null) isInsurance = 0;
		if (isPuc == null) isPuc = 0;
		if (isRepairRequired == null) isRepairRequired = 0;

		log.debug("BIKE_SALE_ENTITY - Created new bike sale: Brand={}, Model={}",
				brandId, modelId);
	}

	@PreUpdate
	protected void onUpdate() {
		log.debug("BIKE_SALE_ENTITY - Updated bike sale: ID={}, Status={}", id, status);
	}
}
