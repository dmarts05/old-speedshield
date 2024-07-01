package com.dmarts05.speedshield.controller;

import com.dmarts05.speedshield.dto.*;
import com.dmarts05.speedshield.exception.InvalidLoginException;
import com.dmarts05.speedshield.exception.UsernameAlreadyTakenException;
import com.dmarts05.speedshield.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest extends ControllerTest {
    @Test
    public void shouldAuthenticateUserWithValidCredentials() throws Exception {
        JwtResponseDto responseDto = new JwtResponseDto("fake-jwt-token", "fake-refresh-token");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser@example.com\", \"password\": \"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("fake-refresh-token"));
    }

    @Test
    public void shouldFailAuthenticationWithInvalidCredentials() throws Exception {
        when(authService.login(any(LoginRequestDto.class))).thenThrow(new InvalidLoginException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"testuser@example.com\", \"password\": \"wrongpassword\"}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldRegisterNewUserWithValidDetails() throws Exception {
        UserEntityDto responseDto = new UserEntityDto(1L, "John Doe", "johndoe@example.com", Role.USER);

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"username\": \"johndoe@example.com\", \"password\": \"password456\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("johndoe@example.com"));
    }

    @Test
    public void shouldFailRegistrationWithDuplicateEmail() throws Exception {
        when(authService.register(any(RegisterRequestDto.class))).thenThrow(new UsernameAlreadyTakenException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"username\": \"johndoe@example.com\", \"password\": \"password456\"}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void shouldRefreshTokenSuccessfully() throws Exception {
        JwtResponseDto responseDto = new JwtResponseDto("new-fake-jwt-token", "new-fake-refresh-token");

        when(authService.refreshToken(any(RefreshTokenRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"fake-jwt-token\", \"refreshToken\": \"fake-refresh-token\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("new-fake-jwt-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("new-fake-refresh-token"));
    }

    @Test
    public void shouldFailLoginWithNullRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldFailRegistrationWithInvalidEmailFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"username\": \"invalidemail\", \"password\": \"password456\"}")
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
