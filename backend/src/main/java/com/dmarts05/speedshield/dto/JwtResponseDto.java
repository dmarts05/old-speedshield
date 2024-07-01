package com.dmarts05.speedshield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    /**
     * JWT access token.
     */
    private String token;

    /**
     * Refresh token used for obtaining new access tokens.
     */
    private String refreshToken;
}
