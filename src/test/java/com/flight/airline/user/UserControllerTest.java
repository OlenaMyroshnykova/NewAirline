package com.flight.airline.user;

import com.flight.airline.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_AdminRole_Success() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> users = userController.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_AdminRole_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userController.getUserById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_AdminRole_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserById(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository).findById(1L);
    }

    @Test
    void deleteUser_AdminRole_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_AdminRole_NotFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // @Test
    // void uploadAvatar_Success() {
    //     // Arrange
    //     MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{});
    //     Principal principal = mock(Principal.class);
    //     when(principal.getName()).thenReturn("user");
    //     when(fileStorageService.saveFile(file, "user")).thenReturn("avatar.png");

    //     // Act
    //     ResponseEntity<?> response = userController.uploadAvatar(file, principal);

    //     // Assert
    //     assertEquals(200, response.getStatusCodeValue());
    //     assertEquals("Avatar uploaded successfully!", ((Map<?, ?>) response.getBody()).get("message"));
    //     verify(fileStorageService).saveFile(file, "user");
    // }

    // @Test
    // void uploadAvatar_Failure() {
    //     // Arrange
    //     MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{});
    //     Principal principal = mock(Principal.class);
    //     when(principal.getName()).thenReturn("user");
    //     when(fileStorageService.saveFile(file, "user")).thenThrow(new RuntimeException("File upload failed!"));

    //     // Act
    //     ResponseEntity<?> response = userController.uploadAvatar(file, principal);

    //     // Assert
    //     assertEquals(500, response.getStatusCodeValue());
    //     assertEquals("File upload failed!", ((Map<?, ?>) response.getBody()).get("message"));
    //     verify(fileStorageService).saveFile(file, "user");
    // }

    @Test
    void getUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setAvatarUrl("avatar.png");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.getUserProfile();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().get("username"));
        assertEquals("avatar.png", response.getBody().get("avatarUrl"));
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void getUserProfile_UserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userController.getUserProfile());
        assertEquals("User not found", thrown.getMessage());
        verify(userRepository).findByUsername("testUser");
    }
}

