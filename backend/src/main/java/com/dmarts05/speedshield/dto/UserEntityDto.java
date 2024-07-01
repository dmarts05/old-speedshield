package com.dmarts05.speedshield.dto;

import com.dmarts05.speedshield.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user information containing id, username, and role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The role of the user.
     */
    private Role role;
}
