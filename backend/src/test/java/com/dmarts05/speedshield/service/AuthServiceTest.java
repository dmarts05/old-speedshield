package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.dto.*;
import com.dmarts05.speedshield.exception.InvalidLoginException;
import com.dmarts05.speedshield.exception.UsernameAlreadyTakenException;
import com.dmarts05.speedshield.model.RefreshTokenEntity;
import com.dmarts05.speedshield.model.Role;
import com.dmarts05.speedshield.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private final String username = "johndoe";
    private final String encodedPassword = "encodedPassword";
    private final String jwtToken = "jwtToken";
    private final String refreshToken = "refreshToken";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void shouldLoginSuccessfully() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .username(username)
                .password(encodedPassword)
                .role(Role.USER)
                .build();
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, "password");
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userService.findByUsername(anyString())).thenReturn(userEntity);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn(jwtToken);
        when(refreshTokenService.generateRefreshToken(any(UserEntity.class))).thenReturn(refreshToken);

        JwtResponseDto response = authService.login(loginRequestDto);
        assertEquals(jwtToken, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    public void shouldThrowInvalidLoginExceptionOnInvalidLogin() {
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new InvalidLoginException());

        assertThrows(InvalidLoginException.class, () -> authService.login(loginRequestDto));
    }

    @Test
    public void shouldRegisterSuccessfully() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("John Doe", username, "password");

        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        UserEntityDto response = authService.register(registerRequestDto);
        assertEquals(username, response.getUsername());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    public void shouldThrowUsernameAlreadyTakenExceptionOnDuplicateRegistration() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("John Doe", username, "password");

        when(userService.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class, () -> authService.register(registerRequestDto));
    }

    @Test
    public void shouldRefreshTokenSuccessfully() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .username(username)
                .password(encodedPassword)
                .role(Role.USER)
                .build();
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto("token", refreshToken);

        when(refreshTokenService.findByToken(anyString())).thenReturn(RefreshTokenEntity.builder()
                .token(refreshToken)
                .userEntity(userEntity)
                .build());
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn(jwtToken);
        when(refreshTokenService.generateRefreshToken(any(UserEntity.class))).thenReturn(refreshToken);

        JwtResponseDto response = authService.refreshToken(refreshTokenRequestDto);
        assertEquals(jwtToken, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }
}
