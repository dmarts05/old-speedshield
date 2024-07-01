package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.exception.UserNotFoundException;
import com.dmarts05.speedshield.model.UserEntity;
import com.dmarts05.speedshield.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service class that provides operations for managing user entities.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructs a UserService instance with the specified UserRepository.
     *
     * @param userRepository Repository for managing user entities.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user entity by username.
     *
     * @param username Username of the user to retrieve.
     * @return UserEntity object associated with the username.
     * @throws UserNotFoundException If no user entity is found with the specified username.
     */
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Checks if a user exists with the specified username.
     *
     * @param username Username to check for existence.
     * @return True if a user exists with the username, false otherwise.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Saves a user entity.
     *
     * @param userEntity User entity to save.
     */
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
}
