package com.flight.airline.auth;

import com.flight.airline.role.Role;
import com.flight.airline.role.RoleRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        String username = "newUser";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.registerUser(username, password);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully!", response.getBody().get("message"));
        assertEquals(username, response.getBody().get("username"));
        assertEquals("ROLE_USER", response.getBody().get("role"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UserAlreadyExists() {
        // Arrange
        String username = "existingUser";
        String password = "password123";

        User existingUser = new User();
        existingUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.registerUser(username, password);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists.", response.getBody().get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_RoleNotFound() {
        // Arrange
        String username = "newUser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(username, password);
        });

        assertEquals("ROLE_USER not found in database", thrown.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}

