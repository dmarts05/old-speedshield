package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.dto.*;
import com.dmarts05.speedshield.exception.InvalidLoginException;
import com.dmarts05.speedshield.exception.UsernameAlreadyTakenException;
import com.dmarts05.speedshield.model.RefreshTokenEntity;
import com.dmarts05.speedshield.model.Role;
import com.dmarts05.speedshield.model.UserEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class that handles authentication and user registration operations.
 */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    /**
     * Constructs an AuthService with required dependencies.
     *
     * @param authenticationManager Authentication manager for handling authentication requests.
     * @param passwordEncoder       Password encoder for encoding passwords.
     * @param jwtService            Service for handling JWT operations.
     * @param refreshTokenService   Service for handling refresh token operations.
     * @param userService           Service for handling user-related operations.
     */
    public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    /**
     * Authenticates a user based on login credentials and generates JWT and refresh tokens.
     *
     * @param loginRequestDto Login request containing username and password.
     * @return JwtResponseDto containing generated JWT and refresh tokens.
     * @throws InvalidLoginException If authentication fails.
     */
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
            if (!authentication.isAuthenticated()) {
                throw new InvalidLoginException();
            }

            UserEntity userEntity = userService.findByUsername(loginRequestDto.getUsername());

            String token = jwtService.generateToken(userEntity);
            String refreshToken = refreshTokenService.generateRefreshToken(userEntity);

            return new JwtResponseDto(token, refreshToken);
        } catch (AuthenticationException e) {
            throw new InvalidLoginException();
        }

    }

    /**
     * Registers a new user with provided registration details.
     *
     * @param registerRequestDto Registration request containing name, username, and password.
     * @return UserEntityDto representing the registered user.
     * @throws UsernameAlreadyTakenException If the username is already taken.
     */
    public UserEntityDto register(RegisterRequestDto registerRequestDto) {
        if (userService.existsByUsername(registerRequestDto.getUsername())) {
            throw new UsernameAlreadyTakenException();
        }

        UserEntity userEntity = UserEntity.builder()
                .name(registerRequestDto.getName())
                .username(registerRequestDto.getUsername())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.USER)
                .build();
        userService.save(userEntity);

        return new UserEntityDto(userEntity.getId(), userEntity.getName(), userEntity.getUsername(), userEntity.getRole());
    }

    /**
     * Generates a new JWT and refresh token based on a valid refresh token.
     *
     * @param refreshTokenRequestDto Refresh token request containing token and refresh token.
     * @return JwtResponseDto containing new JWT and refresh tokens.
     */
    public JwtResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        refreshTokenService.validateRefreshToken(refreshTokenRequestDto.getToken(), refreshTokenRequestDto.getRefreshToken());
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(refreshTokenRequestDto.getRefreshToken());
        UserEntity userEntity = refreshTokenEntity.getUserEntity();

        String token = jwtService.generateToken(userEntity);
        String refreshToken = refreshTokenService.generateRefreshToken(userEntity);
        JwtResponseDto jwtResponseDto = new JwtResponseDto(token, refreshToken);

        // Remove old refresh token
        refreshTokenService.delete(refreshTokenEntity);

        return jwtResponseDto;
    }
}
