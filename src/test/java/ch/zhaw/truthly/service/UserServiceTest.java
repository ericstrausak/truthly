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
        when(userRepository.existsById("valid-id")).thenReturn(true);
        assertTrue(userService.userExists("valid-id"));
        verify(userRepository).existsById("valid-id");
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        when(userRepository.existsById("invalid-id")).thenReturn(false);
        assertFalse(userService.userExists("invalid-id"));
        verify(userRepository).existsById("invalid-id");
    }

    @Test
    @DisplayName("Should handle null user ID")
    void shouldHandleNullUserId() {
        when(userRepository.existsById(null)).thenReturn(false);
        assertFalse(userService.userExists(null));
        verify(userRepository).existsById(null);
    }

    @Test
    @DisplayName("Should handle empty user ID")
    void shouldHandleEmptyUserId() {
        when(userRepository.existsById("")).thenReturn(false);
        assertFalse(userService.userExists(""));
        verify(userRepository).existsById("");
    }
}