package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "stores", indexes = {
		@Index(name = "idx_store_name", columnList = "store_name"),
		@Index(name = "idx_is_active_store", columnList = "is_active"),
		@Index(name = "idx_created_at_store", columnList = "created_at")
})
@SQLDelete(sql = "UPDATE stores SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Store implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(name = "added_by")
	private Integer addedBy;

	@Lob
	@Column(name = "store_name", columnDefinition = "TEXT", nullable = false)
	private String storeName;

	@Lob
	@Column(name = "store_contact_number", columnDefinition = "TEXT", nullable = false)
	private String storeContactNumber;

	@Lob
	@Column(name = "store_gstin_number", columnDefinition = "TEXT")
	private String storeGstinNumber;

	@Lob
	@Column(name = "store_address", columnDefinition = "TEXT", nullable = false)
	private String storeAddress;

	@Lob
	@Column(name = "store_url", columnDefinition = "TEXT")
	private String storeUrl;

	@Lob
	@Column(name = "store_image", columnDefinition = "TEXT")
	private String storeImage;

	@Column(name = "store_latitude")
	private Double storeLatitude;

	@Column(name = "store_longitude")
	private Double storeLongitude;

	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = true)
	private City city;

	@PrePersist
	protected void onCreate() {
		if (isActive == null) isActive = 1;
		if (createdAt == null) createdAt = Timestamp.valueOf(LocalDateTime.now());
		if (updatedAt == null) updatedAt = Timestamp.valueOf(LocalDateTime.now());
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Timestamp.valueOf(LocalDateTime.now());
	}

	public void toggleStatus() {
		this.isActive = (this.isActive != null && this.isActive == 1) ? 0 : 1;
	}
}
