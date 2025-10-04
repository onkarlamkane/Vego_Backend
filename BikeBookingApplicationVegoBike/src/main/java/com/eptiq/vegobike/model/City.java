package com.eptiq.vegobike.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "cities",
        indexes = {
                @Index(name = "idx_city_name", columnList = "city_name"),
                @Index(name = "idx_is_active_city", columnList = "is_active"),
                @Index(name = "idx_created_at_city", columnList = "created_at")
        })
@SQLDelete(sql = "UPDATE cities SET is_active = 0, updated_at = NOW() WHERE id = ?")
//@Where(clause = "is_active != 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "city_name", nullable = false, columnDefinition = "TEXT")
    private String cityName;

    @Column(name = "city_image", columnDefinition = "TEXT")
    private String cityImage;

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}