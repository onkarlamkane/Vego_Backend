package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user_documents")
@Data
@NoArgsConstructor
public class UserDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "is_adhaar_front_verified", nullable = false, columnDefinition = "int default 0")
	private int isAdhaarFrontVerified;

	@Lob
	@Column(name = "adhaar_front_image")
	private String adhaarFrontImage;

	@Column(name = "is_adhaar_back_verified", nullable = false, columnDefinition = "int default 0")
	private int isAdhaarBackVerified;

	@Lob
	@Column(name = "adhaar_back_image")
	private String adhaarBackImage;

	@Column(name = "is_license_verified", nullable = false, columnDefinition = "int default 0")
	private int isLicenseVerified;

	@Lob
	@Column(name = "driving_license_image")
	private String drivingLicenseImage;

	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(name = "is_active", nullable = false, columnDefinition = "int default 1")
	private int isActive;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
}
