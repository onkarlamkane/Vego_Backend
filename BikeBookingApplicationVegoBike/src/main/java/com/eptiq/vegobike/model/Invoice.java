package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Invoice entity for the invoices table.
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "booking_id", nullable = false)
	private int bookingId;

	@Lob
	@Column(name = "invoice_id")
	private String invoiceId;

	@Column(name = "created_at", updatable = false, insertable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Timestamp updatedAt;
}
