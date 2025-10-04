package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the years database table.
 * 
 */
@Entity
@Table(name="years")
@NamedQuery(name="Year.findAll", query="SELECT y FROM Year y")
public class Year implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_active")
	private int isActive;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	private String year;

	public Year() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getIsActive() {
		return this.isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}