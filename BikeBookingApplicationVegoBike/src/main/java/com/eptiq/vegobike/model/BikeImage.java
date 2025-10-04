package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the bike_images database table.
 */
@Entity
@Table(name = "bike_images")
@NamedQuery(name = "BikeImage.findAll", query = "SELECT b FROM BikeImage b")
@Data
public class BikeImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-increment ID
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	private Bike bike;

	@Column(name = "created_at")
	private Timestamp createdAt;

//	@Lob
//	@Column(name = "images")
//	private String images;

	@Column(name = "images", columnDefinition = "LONGTEXT")
	private	String	images;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	public BikeImage() {
	}
}
