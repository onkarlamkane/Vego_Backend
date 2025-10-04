package com.eptiq.vegobike.model;

import com.eptiq.vegobike.enums.ChargeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Entity
@Table(
        name = "late_charges",
        indexes = {
                @Index(name = "idx_category_late_charge", columnList = "category_id"),
                @Index(name = "idx_charge_type", columnList = "charge_type"),
                @Index(name = "idx_is_active_late_charge", columnList = "is_active"),
                @Index(name = "idx_created_at_late_charge", columnList = "created_at")
        }
)
@SQLDelete(sql = "UPDATE late_charges SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LateCharge implements Serializable {

    private static final long serialVersionUID = 1L;

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // Bike category
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    // HOURS or KM
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", nullable = false)
    private ChargeType chargeType;

    // Charge per hour or per km
    @Column(name = "charge", nullable = false)
    private Float charge;

    // Status: 1 = Active, 0 = Inactive
    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    // Auto timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // ===== Lifecycle Callbacks =====

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Timestamp.valueOf(LocalDateTime.now());
        }
        if (updatedAt == null) {
            updatedAt = Timestamp.valueOf(LocalDateTime.now());
        }
        if (isActive == null) {
            isActive = 1;
        }
        log.debug("LATE_CHARGE_PRE_PERSIST - CategoryId: {}, ChargeType: {}, Charge: {}, Status: {}",
                categoryId, chargeType, charge, isActive);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
        log.debug("LATE_CHARGE_PRE_UPDATE - Id: {}, CategoryId: {}, ChargeType: {}, NewCharge: {}, Status: {}",
                id, categoryId, chargeType, charge, isActive);
    }

    @PostPersist
    protected void onPersist() {
        log.info("LATE_CHARGE_PERSISTED - Id: {}, CategoryId: {}, ChargeType: {}, Charge: {}, CreatedAt: {}",
                id, categoryId, chargeType, charge, createdAt);
    }

    @PostUpdate
    protected void onUpdateComplete() {
        log.info("LATE_CHARGE_UPDATED - Id: {}, CategoryId: {}, ChargeType: {}, UpdatedAt: {}",
                id, categoryId, chargeType, updatedAt);
    }

    @PreRemove
    protected void onRemove() {
        log.warn("LATE_CHARGE_REMOVE - Id: {}, CategoryId: {}, ChargeType: {} - Soft delete applied",
                id, categoryId, chargeType);
    }

    // ===== Business Helpers =====
    public boolean isActiveFlag() {
        return isActive != null && isActive == 1;
    }

    public void activate() {
        this.isActive = 1;
        log.debug("LATE_CHARGE_ACTIVATED - Id: {}, CategoryId: {}", id, categoryId);
    }

    public void deactivate() {
        this.isActive = 0;
        log.debug("LATE_CHARGE_DEACTIVATED - Id: {}, CategoryId: {}", id, categoryId);
    }

    public void toggleStatus() {
        this.isActive = (this.isActive != null && this.isActive == 1) ? 0 : 1;
        log.debug("LATE_CHARGE_STATUS_TOGGLED - Id: {}, NewStatus: {}", id, isActive);
    }

    // ===== Equality / ToString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LateCharge)) return false;
        LateCharge that = (LateCharge) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LateCharge{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", chargeType=" + chargeType +
                ", charge=" + charge +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
