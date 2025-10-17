package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Getter
@Setter
public class InvoiceDto {
    private Integer id;
    private String invoiceNumber;
    private Integer bookingId;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}