package ch.zhaw.truthly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import ch.zhaw.truthly.integration.BaseIntegrationTest;
import ch.zhaw.truthly.model.User;
import ch.zhaw.truthly.model.UserCreateDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() throws Exception {
        // Given
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password123");
        userDto.setRole("AUTHOR");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("AUTHOR"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.registrationDate").exists())
                .andReturn();

        // Verify user was saved to database
        assertEquals(1, userRepository.count());
    }

    @ParameterizedTest
    @ValueSource(strings = { "USER", "AUTHOR", "FACT_CHECKER", "ADMIN" })
    @DisplayName("Should accept all valid roles")
    void shouldAcceptValidRoles(String role) throws Exception {
        // Given
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setUsername("testuser_" + role.toLowerCase());
        userDto.setEmail("test_" + role.toLowerCase() + "@example.com");
        userDto.setPassword("password123");
        userDto.setRole(role);

        // When & Then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value(role));
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() throws Exception {
        // Given - Create test users
        User user1 = new User("user1", "user1@test.com", "pass1", "USER");
        User user2 = new User("user2", "user2@test.com", "pass2", "ADMIN");
        userRepository.save(user1);
        userRepository.save(user2);

        // When & Then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("Should return specific user by ID")
    void shouldReturnUserById() throws Exception {
        // Given
        User user = new User("testuser", "test@example.com", "password", "USER");
        User savedUser = userRepository.save(user);

        // When & Then
        mockMvc.perform(get("/api/user/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").value(savedUser.getId()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent user")
    void shouldReturn404ForNonExistentUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle invalid JSON")
    void shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}