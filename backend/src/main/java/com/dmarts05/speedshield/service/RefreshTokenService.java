package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.config.JwtProperties;
import com.dmarts05.speedshield.exception.ExpiredRefreshTokenException;
import com.dmarts05.speedshield.exception.JwtAndRefreshTokenMismatchException;
import com.dmarts05.speedshield.exception.RefreshTokenNotFoundException;
import com.dmarts05.speedshield.model.RefreshTokenEntity;
import com.dmarts05.speedshield.model.UserEntity;
import com.dmarts05.speedshield.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service class that manages operations related to refresh tokens.
 */
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserService userService;

    /**
     * Constructs a RefreshTokenService with required dependencies.
     *
     * @param refreshTokenRepository Repository for managing refresh tokens.
     * @param jwtService             Service for handling JWT operations.
     * @param jwtProperties          JWT properties configuration.
     * @param userService            Service for managing user-related operations.
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService, JwtProperties jwtProperties, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    /**
     * Retrieves a refresh token entity by its token string.
     *
     * @param token Token string of the refresh token.
     * @return RefreshTokenEntity associated with the token.
     * @throws RefreshTokenNotFoundException If the refresh token is not found in the repository.
     */
    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(RefreshTokenNotFoundException::new);
    }

    /**
     * Generates a new refresh token for the specified user entity.
     *
     * @param userEntity User entity for whom the refresh token is generated.
     * @return Token string of the newly generated refresh token.
     */
    public String generateRefreshToken(UserEntity userEntity) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userEntity(userEntity)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiresIn().toMillis()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    /**
     * Deletes the given refresh token entity from the repository.
     *
     * @param refreshTokenEntity Refresh token entity to delete.
     */
    public void delete(RefreshTokenEntity refreshTokenEntity) {
        refreshTokenRepository.delete(refreshTokenEntity);
    }

    /**
     * Deletes all expired refresh tokens from the repository.
     */
    public void deleteAllExpiredTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.deleteAllByExpiryDateBefore(now);
    }

    /**
     * Validates the given refresh token against a JWT token and its associated user.
     *
     * @param token        JWT token to validate against the refresh token.
     * @param refreshToken Refresh token to validate.
     * @throws JwtAndRefreshTokenMismatchException If the JWT and refresh token do not match.
     * @throws ExpiredRefreshTokenException        If the refresh token has expired.
     */
    public void validateRefreshToken(String token, String refreshToken) {
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        UserEntity userEntity = userService.findByUsername(username);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
        boolean doesMatch = refreshTokenEntity.getUserEntity().getId().equals(userEntity.getId());
        if (!doesMatch) {
            throw new JwtAndRefreshTokenMismatchException();
        }
        boolean isExpired = refreshTokenEntity.getExpiryDate().compareTo(Instant.now()) < 0;
        if (isExpired) {
            throw new ExpiredRefreshTokenException();
        }
    }
}
