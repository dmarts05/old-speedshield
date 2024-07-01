package com.dmarts05.speedshield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting token refresh.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDto {
    /**
     * JWT access token.
     */
    private String token;

    /**
     * Refresh token used for obtaining new access tokens.
     */
    private String refreshToken;
}
