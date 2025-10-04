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
@Table(name = "price_lists", indexes = {
		@Index(name = "idx_price_category", columnList = "category_id"),
		@Index(name = "idx_price_days", columnList = "days"),
		@Index(name = "idx_price_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "category_id", nullable = false)
	private Integer categoryId;

	@Column(nullable = false)
	private Integer days;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal deposit;

	@Column(name = "hourly_charge_amount", precision = 10, scale = 2)
	private BigDecimal hourlyChargeAmount;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Column(name = "is_active", nullable = false)
	@Builder.Default
	private Integer isActive = 1;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

	// ✅ Helper methods for business logic
	public boolean isActive() {
		return isActive != null && isActive == 1;
	}

	public void activate() {
		this.isActive = 1;
	}

	public void deactivate() {
		this.isActive = 0;
	}

	public boolean isHourly() {
		return days != null && days == 0;
	}

	public boolean isDaily() {
		return days != null && days > 0;
	}

	@PrePersist
	protected void onCreate() {
		if (isActive == null) isActive = 1;
		log.debug("PRICE_LIST_ENTITY - Created new price: Category={}, Days={}", categoryId, days);
	}

	@PreUpdate
	protected void onUpdate() {
		log.debug("PRICE_LIST_ENTITY - Updated price: ID={}, Active={}", id, isActive);
	}
}
