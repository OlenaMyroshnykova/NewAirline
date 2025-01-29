package com.flight.airline.boocking;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public BookingController(BookingRepository bookingRepository, UserRepository userRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public List<Booking> getUserBookings(Authentication auth) {
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(value -> bookingRepository.findByUserId(value.getId())).orElse(List.of());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createBooking(Authentication auth, @RequestParam String flightNumber) {
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Flight> flight = flightRepository.findByFlightNumber(flightNumber);

        if (user.isEmpty() || flight.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user or flight.");
        }

        Booking booking = new Booking();
        booking.setUser(user.get());
        booking.setFlight(flight.get());
        booking.setBookingTime(LocalDateTime.now());

        return ResponseEntity.ok(bookingRepository.save(booking));
    }
}
