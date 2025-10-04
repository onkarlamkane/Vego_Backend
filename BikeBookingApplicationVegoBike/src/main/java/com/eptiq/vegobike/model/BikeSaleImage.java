package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the bike_sale_images database table.
 * 
 */
@Entity
@Table(name="bike_sale_images")
@NamedQuery(name="BikeSaleImage.findAll", query="SELECT b FROM BikeSaleImage b")
public class BikeSaleImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="bike_id")
	private int bikeId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	private String image;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public BikeSaleImage() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBikeId() {
		return this.bikeId;
	}

	public void setBikeId(int bikeId) {
		this.bikeId = bikeId;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}