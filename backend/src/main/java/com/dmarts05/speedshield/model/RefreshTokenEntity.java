package com.dmarts05.speedshield.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entity class representing a refresh token stored in the database.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    /**
     * Primary key identifier for the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Unique token string representing the refresh token.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Expiry date and time of the refresh token.
     */
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * Associated user entity for whom the refresh token is issued.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;
}
