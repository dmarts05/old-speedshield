package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.config.JwtProperties;
import com.dmarts05.speedshield.exception.JwtNotFoundException;
import com.dmarts05.speedshield.model.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private final String issuer = "testIssuer";
    private final String audience = "testAudience";
    private final long expiresIn = 1000 * 60 * 60; // 1 hour
    private final String secret = "testkeytestkeytestkeytestkeytestkeytestkeytestkeytestkeytestkeytestkeytestkeytestkey";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    @Test
    public void shouldExtractTokenFromHeader() throws JwtNotFoundException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");

        String token = jwtService.extractTokenFromHeader(request);

        assertEquals("testToken", token);
    }

    @Test
    public void shouldThrowExceptionWhenAuthHeaderIsInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        assertThrows(JwtNotFoundException.class, () -> jwtService.extractTokenFromHeader(request));
    }

    @Test
    public void shouldExtractUsername() {
        String token = generateTestToken("testUser");

        when(jwtProperties.getSecret()).thenReturn(secret);
        String username = jwtService.extractUsername(token);

        assertEquals("testUser", username);
    }

    @Test
    public void shouldExtractExpiration() {
        String token = generateTestToken("testUser");

        when(jwtProperties.getSecret()).thenReturn(secret);
        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
    }

    @Test
    public void shouldValidateToken() {
        UserEntity userEntity = UserEntity.builder().username("testUser").build();
        String token = generateTestToken("testUser");

        when(jwtProperties.getSecret()).thenReturn(secret);
        boolean isValid = jwtService.isTokenValid(token, userEntity);

        assertTrue(isValid);
    }

    @Test
    public void shouldNotValidateExpiredToken() {
        UserEntity userEntity = UserEntity.builder().username("testUser").build();
        String token = generateExpiredTestToken("testUser");

        when(jwtProperties.getSecret()).thenReturn(secret);
        boolean isValid = jwtService.isTokenValid(token, userEntity);

        assertFalse(isValid);
    }

    @Test
    public void shouldNotValidateTokenFromAnotherUser() {
        UserEntity userEntity = UserEntity.builder().username("testUser").build();
        String token = generateTestToken("anotherTestUser");

        when(jwtProperties.getSecret()).thenReturn(secret);
        boolean isValid = jwtService.isTokenValid(token, userEntity);

        assertFalse(isValid);
    }

    @Test
    public void shouldGenerateToken() {
        UserEntity userEntity = UserEntity.builder().username("testUser").build();

        when(jwtProperties.getSecret()).thenReturn(secret);
        String token = jwtService.generateToken(userEntity);

        assertNotNull(token);
    }

    @Test
    public void shouldGenerateTokenWithClaims() {
        UserEntity userEntity = UserEntity.builder().username("testUser").build();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");

        when(jwtProperties.getSecret()).thenReturn(secret);
        String token = jwtService.generateToken(claims, userEntity);

        assertNotNull(token);
    }

    private String generateTestToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .audience()
                .add(audience)
                .and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiresIn))
                .signWith(secretKey)
                .compact();
    }

    private String generateExpiredTestToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .audience()
                .add(audience)
                .and()
                .issuedAt(new Date(System.currentTimeMillis() - expiresIn * 2))
                .expiration(new Date(System.currentTimeMillis() - expiresIn))
                .signWith(secretKey)
                .compact();
    }
}
