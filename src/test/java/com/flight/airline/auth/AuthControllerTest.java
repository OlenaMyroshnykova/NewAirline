package com.flight.airline.auth;

import com.flight.airline.role.Role;
import com.flight.airline.role.RoleRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private Role userRole;
    private final String username = "testUser";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setName("ROLE_USER");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        ResponseEntity<Map<String, Object>> response = authController.registerUser(username, password);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User registered successfully!");
        assertThat(response.getBody()).containsEntry("username", username);
        assertThat(response.getBody()).containsEntry("role", "ROLE_USER");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UserAlreadyExists() {
        User existingUser = new User();
        existingUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        ResponseEntity<Map<String, Object>> response = authController.registerUser(username, password);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "User already exists.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_RoleNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> authController.registerUser(username, password));

        assertThat(thrown).hasMessage("ROLE_USER not found in database");
        verify(userRepository, never()).save(any(User.class));
    }
}


