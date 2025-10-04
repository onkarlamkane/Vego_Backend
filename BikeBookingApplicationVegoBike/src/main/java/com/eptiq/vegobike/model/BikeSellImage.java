package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;


/**
 * The persistent class for the bike_sell_images database table.
 *
 */
@Data
@Entity
@Table(name="bike_sell_images")
@NamedQuery(name="BikeSellImage.findAll", query="SELECT b FROM BikeSellImage b")
public class BikeSellImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Lob
	@Column(name="back_images")
	private String backImages;

//	@Column(name="bike_id")
//	private int bikeId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Lob
	@Column(name="front_images")
	private String frontImages;

	@Lob
	@Column(name="left_images")
	private String leftImages;

	@Lob
	@Column(name="right_images")
	private String rightImages;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", referencedColumnName = "id")
	private BikeSale bikeSale;

	public BikeSellImage() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBackImages() {
		return this.backImages;
	}

	public void setBackImages(String backImages) {
		this.backImages = backImages;
	}

//	public int getBikeId() {
//		return this.bikeId;
//	}
//
//	public void setBikeId(int bikeId) {
//		this.bikeId = bikeId;
//	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getFrontImages() {
		return this.frontImages;
	}

	public void setFrontImages(String frontImages) {
		this.frontImages = frontImages;
	}

	public String getLeftImages() {
		return this.leftImages;
	}

	public void setLeftImages(String leftImages) {
		this.leftImages = leftImages;
	}

	public String getRightImages() {
		return this.rightImages;
	}

	public void setRightImages(String rightImages) {
		this.rightImages = rightImages;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}


