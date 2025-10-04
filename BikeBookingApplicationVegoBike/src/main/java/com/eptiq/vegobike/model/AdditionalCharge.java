package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name="additional_charges")
@NamedQuery(name="AdditionalCharge.findAll", query="SELECT a FROM AdditionalCharge a")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalCharge implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Column(name="booking_request_id")
    private BigInteger bookingRequestId;

    @Column(name="charge_type")
    private String chargeType;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="updated_at")
    private Timestamp updatedAt;
}
