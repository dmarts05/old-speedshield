package com.dmarts05.speedshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration containing username and password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    /**
     * Username for registration.
     */
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * Password for registration.
     */
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 32, message = "Password must have from 8 to 32 characters")
    private String password;
}
