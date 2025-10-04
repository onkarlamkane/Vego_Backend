package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
// import org.hibernate.annotations.Where; // Uncomment if you want to add a default filter

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "vehicle_types",
        indexes = {
                @Index(name = "idx_vehicle_type_name", columnList = "name"),
                @Index(name = "idx_is_active_vehicle_type", columnList = "is_active"),
                @Index(name = "idx_created_at_vehicle_type", columnList = "created_at")
        })
@SQLDelete(sql = "UPDATE vehicle_types SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
