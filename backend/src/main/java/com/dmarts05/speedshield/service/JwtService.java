package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.config.JwtProperties;
import com.dmarts05.speedshield.exception.JwtNotFoundException;
import com.dmarts05.speedshield.model.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class that provides operations related to JWT (JSON Web Token) generation, parsing, and validation.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Constructs a JwtService instance with JWT properties.
     *
     * @param jwtProperties JWT properties configuration.
     */
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Extracts the JWT token from the Authorization header of the HTTP request.
     *
     * @param request HTTP servlet request containing the Authorization header.
     * @return Extracted JWT token.
     * @throws JwtNotFoundException If the Authorization header is missing or improperly formatted.
     */
    public String extractTokenFromHeader(HttpServletRequest request) throws JwtNotFoundException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtNotFoundException();
        }
        return authHeader.substring(7);
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT token from which to extract the username.
     * @return Username extracted from the JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token JWT token from which to extract the expiration date.
     * @return Expiration date extracted from the JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim (attribute) from the JWT token.
     *
     * @param token          JWT token from which to extract the claim.
     * @param claimsResolver Function to resolve the specific claim from JWT claims.
     * @param <T>            Type of the claim.
     * @return Claim extracted from the JWT token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims (attributes) from the JWT token.
     *
     * @param token JWT token from which to extract all claims.
     * @return All claims extracted from the JWT token.
     */
    public Claims extractAllClaims(String token) {
        JwtParser jwtParser = Jwts.parser().verifyWith(getSignKey()).build();

        // Extract the claims even if the JWT is expired
        Claims claims;
        try {
            claims = jwtParser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }

        return claims;
    }

    /**
     * Validates whether the JWT token is valid for the specified user entity.
     *
     * @param token      JWT token to validate.
     * @param userEntity User entity against which to validate the JWT token.
     * @return True if the JWT token is valid for the user entity, false otherwise.
     */
    public boolean isTokenValid(String token, UserEntity userEntity) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            String username = extractUsername(token);
            return username.equals(userEntity.getUsername());
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Generates a JWT token for the specified user entity.
     *
     * @param userEntity User entity for which to generate the JWT token.
     * @return Generated JWT token.
     */
    public String generateToken(UserEntity userEntity) {
        return createToken(new HashMap<>(), userEntity.getUsername());
    }

    /**
     * Generates a JWT token with additional claims for the specified user entity.
     *
     * @param claims     Additional claims to include in the JWT token.
     * @param userEntity User entity for which to generate the JWT token.
     * @return Generated JWT token with additional claims.
     */
    public String generateToken(Map<String, Object> claims, UserEntity userEntity) {
        return createToken(claims, userEntity.getUsername());
    }

    /**
     * Creates a JWT token with specified claims and username.
     *
     * @param claims   Claims to include in the JWT token.
     * @param username Username (subject) for the JWT token.
     * @return Created JWT token.
     */
    private String createToken(Map<String, Object> claims, String username) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + jwtProperties.getExpiresIn().toMillis());
        return Jwts.builder()
                .subject(username)
                .issuer(jwtProperties.getIssuer())
                .audience()
                .add(jwtProperties.getAudience())
                .and()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claims(claims)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Retrieves the signing key for JWT token verification.
     *
     * @return Signing key used for JWT token verification.
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
