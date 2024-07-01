package com.dmarts05.speedshield.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for cleaning up expired refresh tokens from the database.
 */
@Service
public class RefreshTokenCleanupService {
    private final RefreshTokenService refreshTokenService;

    /**
     * Constructs a RefreshTokenCleanupService with the required dependencies.
     *
     * @param refreshTokenService Service for managing refresh tokens.
     */
    public RefreshTokenCleanupService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }


    /**
     * Scheduled method to cleanup expired refresh tokens from the database.
     * This method runs at midnight every day (00:00:00).
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() {
        refreshTokenService.deleteAllExpiredTokens();
    }
}
