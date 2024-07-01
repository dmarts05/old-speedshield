package com.dmarts05.speedshield.controller;

import com.dmarts05.speedshield.dto.*;
import com.dmarts05.speedshield.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for login and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authService The user service.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates the user with the provided login details.
     *
     * @param loginRequestDto The login details.
     * @return ResponseEntity with the JWT token and refresh token.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtResponseDto jwtResponseDto = authService.login(loginRequestDto);
        return new ResponseEntity<>(jwtResponseDto, HttpStatus.CREATED);
    }

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerRequestDto The registration details.
     * @return ResponseEntity with the created user details.
     */
    @PostMapping("/register")
    public ResponseEntity<UserEntityDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        UserEntityDto userEntityDto = authService.register(registerRequestDto);
        return new ResponseEntity<>(userEntityDto, HttpStatus.CREATED);
    }

    /**
     * Refreshes the JWT token with the provided refresh token request.
     *
     * @param refreshTokenRequestDto The refresh token request.
     * @return The new JWT token and refresh token.
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        JwtResponseDto jwtResponseDto = authService.refreshToken(refreshTokenRequestDto);
        return new ResponseEntity<>(jwtResponseDto, HttpStatus.CREATED);
    }

    /**
     * Pings the server to check if it is running requiring authentication.
     *
     * @return ResponseEntity with the message "Pong".
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Pong", HttpStatus.OK);
    }
}
