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
import java.util.Objects;

@Slf4j
@Entity
@Table(name = "models", indexes = {
		@Index(name = "idx_model_name", columnList = "model_name"),
		@Index(name = "idx_is_active_model", columnList = "is_active"),
		@Index(name = "idx_created_at_model", columnList = "created_at"),
		@Index(name = "idx_brand_id_model", columnList = "brand_id")
})
@SQLDelete(sql = "UPDATE models SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Model implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer id;

	@Column(name = "brand_id", nullable = false)
	private Integer brandId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id", insertable = false, updatable = false)
	private Brand brand;

	@Lob
	@Column(name = "model_name", columnDefinition = "TEXT", nullable = false)
	private String modelName;

	@Lob
	@Column(name = "model_image", columnDefinition = "TEXT")
	private String modelImage;

	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

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

	public void toggleStatus() { this.isActive = (this.isActive != null && this.isActive == 1) ? 0 : 1; }

	@Override public boolean equals(Object o){ if(this==o)return true; if(!(o instanceof Model m))return false; return Objects.equals(id,m.id); }
	@Override public int hashCode(){ return Objects.hash(id); }
}
