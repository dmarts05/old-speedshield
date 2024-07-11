package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.config.JwtProperties;
import com.dmarts05.speedshield.exception.ExpiredRefreshTokenException;
import com.dmarts05.speedshield.exception.JwtAndRefreshTokenMismatchException;
import com.dmarts05.speedshield.model.RefreshTokenEntity;
import com.dmarts05.speedshield.model.UserEntity;
import com.dmarts05.speedshield.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    private static String jwtToken;
    private static String refreshToken;
    private static String username;
    private static Duration refreshExpiresIn;
    private static UserEntity userEntity;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private UserService userService;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeAll
    public static void beforeAll() {
        jwtToken = "jwtToken";
        refreshToken = "refreshToken";
        username = "testUser";
        refreshExpiresIn = Duration.ofDays(30);
        userEntity = UserEntity.builder().id(1L).username(username).build();
    }

    @Test
    public void shouldGenerateRefreshToken() {
        when(jwtProperties.getRefreshExpiresIn()).thenReturn(refreshExpiresIn);

        String token = refreshTokenService.generateRefreshToken(userEntity);

        assertNotNull(token);
        verify(refreshTokenRepository, times(1)).save(any(RefreshTokenEntity.class));
    }

    @Test
    public void shouldValidateRefreshTokenSuccessfully() {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .userEntity(userEntity)
                .token(refreshToken)
                .expiryDate(Instant.now().plus(Duration.ofDays(1)))
                .build();
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(userEntity);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));

        assertDoesNotThrow(() -> refreshTokenService.validateRefreshToken(jwtToken, refreshToken));
    }

    @Test
    public void shouldThrowJwtAndRefreshTokenMismatchException() {
        UserEntity differentUser = UserEntity.builder().id(2L).username("differentUser").build();
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .userEntity(differentUser)
                .token(refreshToken)
                .expiryDate(Instant.now().plus(Duration.ofDays(1)))
                .build();
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(userEntity);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));

        assertThrows(JwtAndRefreshTokenMismatchException.class, () -> refreshTokenService.validateRefreshToken(jwtToken, refreshToken));
    }

    @Test
    public void shouldThrowExpiredRefreshTokenException() {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .userEntity(userEntity)
                .token(refreshToken)
                .expiryDate(Instant.now().minus(Duration.ofDays(1)))
                .build();

        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(userEntity);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));

        assertThrows(ExpiredRefreshTokenException.class, () -> refreshTokenService.validateRefreshToken(jwtToken, refreshToken));
    }
}
