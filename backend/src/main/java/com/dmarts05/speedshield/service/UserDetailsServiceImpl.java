package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class that implements Spring Security UserDetailsService to load user details
 * from UserRepository based on username.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Constructs the CustomUserDetailsService with a UserRepository dependency.
     *
     * @param userRepository The repository for user data access.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by username.
     *
     * @param username The username to load user details for.
     * @return UserDetails object containing user details.
     * @throws UsernameNotFoundException If user with given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with given username not found"));
    }
}
