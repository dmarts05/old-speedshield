package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.config.JwtProperties;
import com.dmarts05.speedshield.exception.JwtNotFoundException;
import com.dmarts05.speedshield.model.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private static UserEntity userEntity;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    @BeforeAll
    public static void beforeAll() {
        userEntity = UserEntity.builder().id(1L).username("testUser").build();
    }

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
        setUpMocks();
        String token = jwtService.generateToken(userEntity);
        String username = jwtService.extractUsername(token);
        assertEquals(userEntity.getUsername(), username);
    }

    @Test
    public void shouldExtractUsernameExpiredToken() {
        setUpMocks();
        String token = generateExpiredToken(userEntity);
        String username = jwtService.extractUsername(token);
        assertEquals(userEntity.getUsername(), username);
    }

    @Test
    public void shouldExtractExpiration() {
        setUpMocks();
        String token = jwtService.generateToken(userEntity);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
    }

    @Test
    public void shouldExtractExpirationExpiredToken() {
        setUpMocks();
        String token = generateExpiredToken(userEntity);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
    }

    @Test
    public void shouldValidateToken() {
        setUpMocks();
        String token = jwtService.generateToken(userEntity);
        boolean isValid = jwtService.isTokenValid(token, userEntity);
        assertTrue(isValid);
    }

    @Test
    public void shouldNotValidateExpiredToken() {
        setUpMocks();
        String token = generateExpiredToken(userEntity);
        boolean isValid = jwtService.isTokenValid(token, userEntity);
        assertFalse(isValid);
    }

    @Test
    public void shouldNotValidateTokenFromAnotherUser() {
        setUpMocks();
        UserEntity anotherTestUser = UserEntity.builder().username("anotherTestUser").build();
        String token = jwtService.generateToken(anotherTestUser);
        boolean isValid = jwtService.isTokenValid(token, userEntity);
        assertFalse(isValid);
    }

    @Test
    public void shouldGenerateToken() {
        setUpMocks();
        String token = jwtService.generateToken(userEntity);
        assertNotNull(token);
    }

    @Test
    public void shouldGenerateTokenWithClaims() {
        setUpMocks();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        String token = jwtService.generateToken(claims, userEntity);
        assertNotNull(token);
        assertEquals("admin", jwtService.extractClaim(token, (c) -> c.get("role")));
    }

    private String generateExpiredToken(UserEntity userEntity) {
        setUpMocks();
        Map<String, Object> claims = new HashMap<>();
        claims.put("exp", new Date(System.currentTimeMillis() - 1000));
        claims.put("iat", new Date(System.currentTimeMillis() - 2000));
        return jwtService.generateToken(claims, userEntity);
    }

    private void setUpMocks() {
        when(jwtProperties.getSecret()).thenReturn("906424b7eee97ae4d23f4be0ed18885c439918ad0f509958902945301cf4d1e7");
        when(jwtProperties.getExpiresIn()).thenReturn(Duration.ofMinutes(15));
        when(jwtProperties.getIssuer()).thenReturn("https://speedshield.dmarts05.com");
        when(jwtProperties.getAudience()).thenReturn("https://speedshield.dmarts05.com");
    }
}
