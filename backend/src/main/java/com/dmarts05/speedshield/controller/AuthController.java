package com.dmarts05.speedshield.controller;

import com.dmarts05.speedshield.dto.LoginDto;
import com.dmarts05.speedshield.dto.RegisterDto;
import com.dmarts05.speedshield.dto.UserDto;
import com.dmarts05.speedshield.exception.InvalidLoginException;
import com.dmarts05.speedshield.exception.UsernameAlreadyTakenException;
import com.dmarts05.speedshield.model.Role;
import com.dmarts05.speedshield.model.UserEntity;
import com.dmarts05.speedshield.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authenticationManager The authentication manager.
     * @param userRepository        The user repository.
     * @param passwordEncoder       The password encoder.
     */
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates the user with the provided login details.
     *
     * @param request  The HTTP servlet request.
     * @param loginDto The login details.
     * @return ResponseEntity with no content if authentication is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, @Valid @RequestBody LoginDto loginDto) {
        // Authenticate user
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        } catch (AuthenticationException ex) {
            throw new InvalidLoginException();
        }

        // Save authenticated user in context
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        // Send session cookie
        request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerDto The registration details.
     * @return ResponseEntity with the created user details.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) {
        if (userRepository.existsUserEntityByUsername(registerDto.getUsername())) {
            throw new UsernameAlreadyTakenException();
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getRole());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}
