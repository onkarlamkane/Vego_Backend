package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the password_resets database table.
 * 
 */
@Entity
@Table(name="password_resets")
@NamedQuery(name="PasswordReset.findAll", query="SELECT p FROM PasswordReset p")
public class PasswordReset implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	private String email;

	private String token;


	public PasswordReset() {
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}