package com.dmarts05.speedshield.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtPropertiesTest {

    @Autowired
    JwtProperties jwtProperties;

    @Test
    void shouldLoadJwtProperties() {
        String audience = jwtProperties.getAudience();
        assertThat(audience).isNotEmpty();

        String issuer = jwtProperties.getIssuer();
        assertThat(issuer).isNotEmpty();

        String secret = jwtProperties.getSecret();
        assertThat(secret).isNotEmpty();

        Duration expiresIn = jwtProperties.getExpiresIn();
        assertThat(expiresIn).isNotNull();

        Duration refreshExpiresIn = jwtProperties.getRefreshExpiresIn();
        assertThat(refreshExpiresIn).isNotNull();
    }
}