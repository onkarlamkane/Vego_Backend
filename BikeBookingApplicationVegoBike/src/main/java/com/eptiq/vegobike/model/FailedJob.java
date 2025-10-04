package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the failed_jobs database table.
 * 
 */
@Entity
@Table(name="failed_jobs")
@NamedQuery(name="FailedJob.findAll", query="SELECT f FROM FailedJob f")
public class FailedJob implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Lob
	private String connection;

	@Lob
	private String exception;

	@Column(name="failed_at")
	private Timestamp failedAt;

	@Lob
	private String payload;

	@Lob
	private String queue;

	private String uuid;

	public FailedJob() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getConnection() {
		return this.connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getException() {
		return this.exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Timestamp getFailedAt() {
		return this.failedAt;
	}

	public void setFailedAt(Timestamp failedAt) {
		this.failedAt = failedAt;
	}

	public String getPayload() {
		return this.payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getQueue() {
		return this.queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}