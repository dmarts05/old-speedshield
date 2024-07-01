package com.dmarts05.speedshield.service;

import com.dmarts05.speedshield.exception.UserNotFoundException;
import com.dmarts05.speedshield.model.UserEntity;
import com.dmarts05.speedshield.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final String username = "testUser";
    private final UserEntity userEntity = UserEntity.builder().username(username).build();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void shouldFindByUsernameWhenUserExists() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        UserEntity foundUser = userService.findByUsername(username);
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    public void shouldFindByUsernameWhenUserDoesNotExist() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername(username));
    }
}
