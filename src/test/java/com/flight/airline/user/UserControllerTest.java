package com.flight.airline.user;

import com.flight.airline.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setAvatarUrl("http://example.com/avatar.png");
    }

    @Test
    void getAllUsers_AdminRole_Success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser, new User()));

        List<User> users = userController.getAllUsers();

        assertThat(users).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_AdminRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testUser);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_AdminRole_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userRepository).findById(1L);
    }

    @Test
    void deleteUser_AdminRole_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_AdminRole_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // @Test
    // void uploadAvatar_Success() {
    //     MultipartFile file = mock(MultipartFile.class);
    //     Principal principal = mock(Principal.class);
    //     when(principal.getName()).thenReturn("testUser");
    //     when(fileStorageService.saveFile(file, "testUser")).thenReturn("avatar.png");

    //     ResponseEntity<?> response = userController.uploadAvatar(file, principal);

    //     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    //     assertThat(response.getBody()).isInstanceOf(Map.class);
    //     assertThat(((Map<?, ?>) response.getBody())).containsEntry("message", "Avatar uploaded successfully!");
    //     verify(fileStorageService).saveFile(file, "testUser");
    // }

    // @Test
    // void uploadAvatar_Fail() {
    //     MultipartFile file = mock(MultipartFile.class);
    //     Principal principal = mock(Principal.class);
    //     when(principal.getName()).thenReturn("testUser");
    //     when(fileStorageService.saveFile(file, "testUser")).thenThrow(new RuntimeException("File upload failed!"));

    //     ResponseEntity<?> response = userController.uploadAvatar(file, principal);

    //     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    //     assertThat(((Map<?, ?>) response.getBody())).containsEntry("message", "File upload failed!");
    // }

    @Test
    void getUserProfile_UserNotFound() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, userController::getUserProfile);

        assertThat(thrown).hasMessage("User not found");
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void getUserProfile_Success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        ResponseEntity<Map<String, Object>> response = userController.getUserProfile();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("username", "testUser");
        assertThat(response.getBody()).containsEntry("avatarUrl", "http://example.com/avatar.png");
    }
}


