package com.dmarts05.speedshield.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login containing username and password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    /**
     * Username for login.
     */
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * Password for login.
     */
    @NotBlank(message = "Password is mandatory")
    private String password;
}
