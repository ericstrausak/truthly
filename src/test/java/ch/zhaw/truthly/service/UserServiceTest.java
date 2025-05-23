package ch.zhaw.truthly.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.zhaw.truthly.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return true when user exists")
    void shouldReturnTrueWhenUserExists() {
        // Given
        String userId = "user123";
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        boolean result = userService.userExists(userId);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        // Given
        String userId = "nonexistent";
        when(userRepository.existsById(userId)).thenReturn(false);

        // When
        boolean result = userService.userExists(userId);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(userId);
    }
}