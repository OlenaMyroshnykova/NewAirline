package com.flight.airline;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.role.Role;
import com.flight.airline.role.RoleRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FlightRepository flightRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, FlightRepository flightRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.flightRepository = flightRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();            
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }
    }
    @PostConstruct
    public void loadFlights() {
        System.out.println();
//        if (flightRepository.count() == 0) {
            List<Flight> flights = List.<Flight>of(
                new Flight("AF123", "Madrid", "Paris", LocalDateTime.of(2025, 2, 1, 12, 0)),
                new Flight("LH456", "Berlin", "London", LocalDateTime.of(2025, 2, 2, 14, 30)),
                new Flight("BA789", "Barcelona", "New York", LocalDateTime.of(2025, 2, 3, 9, 15))
            );
            flightRepository.saveAll(flights);
//        }
    }
}

