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

/**
 * Category Entity for VegoBike Application
 *
 * This entity represents product categories in the system.
 * Implements soft delete functionality and automatic audit trail.
 *
 * @author VegoBike Team
 * @version 1.0
 * @since 2025-08-26
 */
@Slf4j
@Entity
@Table(
		name = "categories",
		indexes = {
				@Index(name = "idx_category_name", columnList = "category_name"),
				@Index(name = "idx_is_active", columnList = "is_active"),
				@Index(name = "idx_created_at", columnList = "created_at")
		}
)
@SQLDelete(sql = "UPDATE categories SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Primary key for the category
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	/**
	 * Category name with validation constraints
	 */
	@NotBlank(message = "Category name cannot be blank")
	@Size(min = 2, max = 255, message = "Category name must be between 2 and 255 characters")
	@Column(name = "category_name", columnDefinition = "TEXT", nullable = false)
	private String categoryName;

	/**
	 * Image file path/URL for the category
	 */
	@Lob
	@Column(name = "image", columnDefinition = "TEXT")
	private String image;

	/**
	 * Status flag: 1 = Active, 0 = Inactive (Soft Delete)
	 * Default value is 1 (Active)
	 */
	@Builder.Default
	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;

	/**
	 * Timestamp when the record was created
	 * Automatically managed by Hibernate
	 */
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	/**
	 * Timestamp when the record was last updated
	 * Automatically managed by Hibernate
	 */
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

	@ManyToOne
	@JoinColumn(name="vehicle_type_id", nullable=true)
	private VehicleType vehicleType;


	// ========== JPA LIFECYCLE CALLBACKS ==========

	/**
	 * Called before the entity is persisted to the database
	 * Sets default values and performs validation
	 */
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

		log.debug("CATEGORY_ENTITY_PRE_PERSIST - CategoryName: {}, IsActive: {}, Timestamp: {}",
				maskCategoryName(categoryName), isActive, LocalDateTime.now());
	}

	/**
	 * Called before the entity is updated in the database
	 * Updates the timestamp and logs the change
	 */
	@PreUpdate
	protected void onUpdate() {
		updatedAt = Timestamp.valueOf(LocalDateTime.now());

		log.debug("CATEGORY_ENTITY_PRE_UPDATE - CategoryId: {}, CategoryName: {}, IsActive: {}, Timestamp: {}",
				id, maskCategoryName(categoryName), isActive, LocalDateTime.now());
	}

	/**
	 * Called after the entity is loaded from the database
	 */
	@PostLoad
	protected void onLoad() {
		log.trace("CATEGORY_ENTITY_POST_LOAD - CategoryId: {}, CategoryName: {}",
				id, maskCategoryName(categoryName));
	}

	/**
	 * Called after the entity is persisted to the database
	 */
	@PostPersist
	protected void onPersist() {
		log.info("CATEGORY_ENTITY_PERSISTED - CategoryId: {}, CategoryName: {}, CreatedAt: {}",
				id, maskCategoryName(categoryName), createdAt);
	}

	/**
	 * Called after the entity is updated in the database
	 */
	@PostUpdate
	protected void onUpdateComplete() {
		log.info("CATEGORY_ENTITY_UPDATED - CategoryId: {}, CategoryName: {}, UpdatedAt: {}",
				id, maskCategoryName(categoryName), updatedAt);
	}

	/**
	 * Called before the entity is removed from the database
	 */
	@PreRemove
	protected void onRemove() {
		log.warn("CATEGORY_ENTITY_REMOVE - CategoryId: {}, CategoryName: {} - Performing soft delete",
				id, maskCategoryName(categoryName));
	}

	// ========== BUSINESS METHODS ==========

	/**
	 * Checks if the category is active
	 * @return true if category is active (is_active = 1)
	 */
	public boolean isActive() {
		return isActive != null && isActive == 1;
	}

	/**
	 * Activates the category
	 */
	public void activate() {
		this.isActive = 1;
		log.debug("CATEGORY_ACTIVATED - CategoryId: {}, CategoryName: {}", id, maskCategoryName(categoryName));
	}

	/**
	 * Deactivates the category (soft delete)
	 */
	public void deactivate() {
		this.isActive = 0;
		log.debug("CATEGORY_DEACTIVATED - CategoryId: {}, CategoryName: {}", id, maskCategoryName(categoryName));
	}

	/**
	 * Toggles the category status
	 */
	public void toggleStatus() {
		this.isActive = (this.isActive == 1) ? 0 : 1;
		log.debug("CATEGORY_STATUS_TOGGLED - CategoryId: {}, CategoryName: {}, NewStatus: {}",
				id, maskCategoryName(categoryName), isActive);
	}

	/**
	 * Updates the category name with validation
	 * @param newCategoryName the new category name
	 */
	public void updateCategoryName(String newCategoryName) {
		if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
			throw new IllegalArgumentException("Category name cannot be null or empty");
		}

		String oldName = this.categoryName;
		this.categoryName = newCategoryName.trim();

		log.debug("CATEGORY_NAME_UPDATED - CategoryId: {}, OldName: {}, NewName: {}",
				id, maskCategoryName(oldName), maskCategoryName(newCategoryName));
	}

	/**
	 * Updates the category image
	 * @param newImage the new image path/URL
	 */
	public void updateImage(String newImage) {
		String oldImage = this.image;
		this.image = newImage;

		log.debug("CATEGORY_IMAGE_UPDATED - CategoryId: {}, CategoryName: {}, HasOldImage: {}, HasNewImage: {}",
				id, maskCategoryName(categoryName), oldImage != null, newImage != null);
	}

	// ========== UTILITY METHODS ==========

	/**
	 * Masks category name for logging (privacy/security)
	 * @param categoryName the category name to mask
	 * @return masked category name
	 */
	private String maskCategoryName(String categoryName) {
		if (categoryName == null || categoryName.length() <= 4) {
			return "****";
		}
		return categoryName.substring(0, 2) + "****" + categoryName.substring(categoryName.length() - 2);
	}

	// ========== OVERRIDE METHODS FOR BETTER PERFORMANCE ==========

	/**
	 * Custom equals method for better performance and null safety
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return Objects.equals(id, category.id);
	}

	/**
	 * Custom hashCode method for better performance and null safety
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Custom toString method with masked data for logging
	 */
	@Override
	public String toString() {
		return "Category{" +
				"id=" + id +
				", categoryName='" + maskCategoryName(categoryName) + '\'' +
				", hasImage=" + (image != null && !image.isEmpty()) +
				", isActive=" + isActive +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}
}
