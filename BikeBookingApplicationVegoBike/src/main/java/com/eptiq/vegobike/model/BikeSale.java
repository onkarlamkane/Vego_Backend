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
