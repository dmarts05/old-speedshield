package com.dmarts05.speedshield.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuration properties for JWT (JSON Web Token) related settings.
 */
@AllArgsConstructor
@Getter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /**
     * Secret used for JWT signing and verification.
     */
    @NotNull
    private final String secret;

    /**
     * Issuer of the JWT.
     */
    @NotEmpty
    private final String issuer;

    /**
     * Audience for whom the JWT is intended.
     */
    @NotEmpty
    private final String audience;

    /**
     * Duration after which the JWT expires.
     */
    @NotNull
    @DurationMin(seconds = 1)
    private final Duration expiresIn;

    /**
     * Duration after which the refresh token expires.
     */
    @NotNull
    @DurationMin(seconds = 1)
    private final Duration refreshExpiresIn;
}
