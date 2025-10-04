package com.eptiq.vegobike.model;

import com.eptiq.vegobike.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "bike_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "brand_id", nullable = false)
    private Integer brandId;

    @Column(name = "model_id", nullable = false)
    private Integer modelId;

    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Lob
    @Column(name = "service_description")
    private String serviceDescription;

    @Lob
    @Column(name = "service_image")
    private String serviceImage;

    @Column(precision = 38, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "status", nullable = false)
    private Integer statusCode = 1;   // default ACTIVE

    @Column(name = "service_type", nullable = false)
    private Integer serviceTypeCode = 1; // default type 1


    // --- Expose clean values ---

    @Transient
    public ServiceType getServiceType() {
        return ServiceType.fromCode(this.serviceTypeCode);
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceTypeCode = (serviceType != null) ? serviceType.getCode() : null;
    }

    @Transient
    public String getStatus() {
        return (this.statusCode != null && this.statusCode == 1) ? "ACTIVE" : "INACTIVE";
    }

    public void setStatus(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            this.statusCode = 1;
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            this.statusCode = 0;
        }
    }
}
