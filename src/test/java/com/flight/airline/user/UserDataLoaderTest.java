package com.flight.airline.user;

import com.flight.airline.role.Role;
import com.flight.airline.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import static org.mockito.Mockito.*;

class UserDataLoaderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDataLoader userDataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRun_CreatesRolesAndUsersIfNotExist() {
        when(roleRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        
        userDataLoader.run();
        
        verify(roleRepository, times(2)).save(any(Role.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testRun_DoesNotCreateRolesOrUsersIfTheyExist() {
        when(roleRepository.count()).thenReturn(2L);
        when(userRepository.count()).thenReturn(2L);

        userDataLoader.run();

        verify(roleRepository, never()).save(any(Role.class));
        verify(userRepository, never()).save(any(User.class));
    }
}

