package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Entity
@Table(
		name = "brands",
		indexes = {
				@Index(name = "idx_brand_name", columnList = "brand_name"),
				@Index(name = "idx_is_active_brand", columnList = "is_active"),
				@Index(name = "idx_created_at_brand", columnList = "created_at")
		}
)
@SQLDelete(sql = "UPDATE brands SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand implements Serializable {


	private static final long serialVersionUID = 1L;

	// Primary key
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	// Brand name with validation
	@NotBlank(message = "Brand name cannot be blank")
	@Size(min = 2, max = 255, message = "Brand name must be between 2 and 255 characters")
	@Lob
	@Column(name = "brand_name", columnDefinition = "TEXT", nullable = false)
	private String brandName;

	// Brand image path/URL (optional)
	@Lob
	@Column(name = "brand_image", columnDefinition = "TEXT")
	private String brandImage;

	// Optional: link to category (kept nullable per your schema)
	@Column(name = "category_id")
	private Integer categoryId;

	// Status flag: 1 = Active, 0 = Inactive (Soft Delete)
	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;


	// Automatic timestamps
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

// ========== LIFECYCLE CALLBACKS ==========

	@PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = Timestamp.valueOf(LocalDateTime.now());
		}
		if (updatedAt == null) {
			updatedAt = Timestamp.valueOf(LocalDateTime.now());
		}
		if (isActive == null) {
			isActive = 1;
		}
		log.debug("BRAND_ENTITY_PRE_PERSIST - BrandName: {}, IsActive: {}, Timestamp: {}",
				maskName(brandName), isActive, LocalDateTime.now());
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Timestamp.valueOf(LocalDateTime.now());
		log.debug("BRAND_ENTITY_PRE_UPDATE - BrandId: {}, BrandName: {}, IsActive: {}, Timestamp: {}",
				id, maskName(brandName), isActive, LocalDateTime.now());
	}

	@PostLoad
	protected void onLoad() {
		log.trace("BRAND_ENTITY_POST_LOAD - BrandId: {}, BrandName: {}",
				id, maskName(brandName));
	}

	@PostPersist
	protected void onPersist() {
		log.info("BRAND_ENTITY_PERSISTED - BrandId: {}, BrandName: {}, CreatedAt: {}",
				id, maskName(brandName), createdAt);
	}

	@PostUpdate
	protected void onUpdateComplete() {
		log.info("BRAND_ENTITY_UPDATED - BrandId: {}, BrandName: {}, UpdatedAt: {}",
				id, maskName(brandName), updatedAt);
	}

	@PreRemove
	protected void onRemove() {
		log.warn("BRAND_ENTITY_REMOVE - BrandId: {}, BrandName: {} - Performing soft delete",
				id, maskName(brandName));
	}

// ========== BUSINESS HELPERS ==========

	// Convenience flag method; avoids boolean-style getter name conflicts
	public boolean isActiveFlag() {
		return isActive != null && isActive == 1;
	}

	public void activate() {
		this.isActive = 1;
		log.debug("BRAND_ACTIVATED - BrandId: {}, BrandName: {}", id, maskName(brandName));
	}

	public void deactivate() {
		this.isActive = 0;
		log.debug("BRAND_DEACTIVATED - BrandId: {}, BrandName: {}", id, maskName(brandName));
	}

	public void toggleStatus() {
		this.isActive = (this.isActive != null && this.isActive == 1) ? 0 : 1;
		log.debug("BRAND_STATUS_TOGGLED - BrandId: {}, BrandName: {}, NewStatus: {}",
				id, maskName(brandName), isActive);
	}

	public void updateBrandName(String newName) {
		if (newName == null || newName.trim().isEmpty()) {
			throw new IllegalArgumentException("Brand name cannot be null or empty");
		}
		String oldName = this.brandName;
		this.brandName = newName.trim();
		log.debug("BRAND_NAME_UPDATED - BrandId: {}, OldName: {}, NewName: {}",
				id, maskName(oldName), maskName(newName));
	}

	public void updateBrandImage(String newImage) {
		String oldImg = this.brandImage;
		this.brandImage = newImage;
		log.debug("BRAND_IMAGE_UPDATED - BrandId: {}, BrandName: {}, HadOldImage: {}, HasNewImage: {}",
				id, maskName(brandName), oldImg != null, newImage != null);
	}

// ========== UTILITY ==========

	private String maskName(String name) {
		if (name == null || name.length() <= 4) return "****";
		return name.substring(0, 2) + "****" + name.substring(name.length() - 2);
	}

// ========== EQUALITY / TOSTRING ==========

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Brand)) return false;
		Brand brand = (Brand) o;
		return Objects.equals(id, brand.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Brand{" +
				"id=" + id +
				", brandName='" + maskName(brandName) + '\'' +
				", hasImage=" + (brandImage != null && !brandImage.isEmpty()) +
				", isActive=" + isActive +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}

}