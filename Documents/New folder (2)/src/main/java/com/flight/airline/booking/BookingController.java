package com.flight.airline.booking;

//mport org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.flight.airline.exception.BookingAlreadyExistsException;
import com.flight.airline.exception.FlightNotFoundException;
import com.flight.airline.exception.UserNotFoundException;
import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
//import java.util.Optional;

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
    public ResponseEntity<List<Booking>> getUserBookings(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> createBooking(Authentication auth, @RequestParam String flightNumber) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found: " + flightNumber));

        // Проверяем, не бронировал ли этот пользователь уже этот рейс
        boolean alreadyBooked = bookingRepository.findByUserId(user.getId())
                .stream().anyMatch(booking -> booking.getFlight().getId().equals(flight.getId()));

        if (alreadyBooked) {
            throw new BookingAlreadyExistsException("You have already booked this flight: " + flightNumber);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of(
                "message", "Booking created successfully!",
                "flightNumber", flightNumber,
                "user", username
        ));
    }
}

