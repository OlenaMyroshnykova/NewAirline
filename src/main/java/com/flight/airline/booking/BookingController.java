package com.flight.airline.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/admin/bookings/history/{username}")
    public ResponseEntity<List<Booking>> getUserBookingHistory(@PathVariable String username) {
        List<Booking> bookings = bookingService.getBookingsByUsername(username);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/users/bookings/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserBookings(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        if (bookings.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No bookings found"));
        }

        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/users/book/{flightId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Booking> bookFlight(Authentication authentication, @PathVariable Long flightId) {
        String username = authentication.getName();
        return ResponseEntity.ok(bookingService.bookFlight(username, flightId));
    }

    @PutMapping("/admin/bookings/{bookingId}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/admin/bookings/{bookingId}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/users/{userId}/bookings/status/{status}")
    public ResponseEntity<List<Booking>> getUserBookingsByStatus(@PathVariable Long userId, @PathVariable BookingStatus status) {
        return ResponseEntity.ok(bookingService.getUserBookingsByStatus(userId, status));
    }

    @PutMapping("/admin/bookings/{bookingId}/status/{status}")
    public ResponseEntity<Void> updateBookingStatus(@PathVariable Long bookingId, @PathVariable BookingStatus status) {
        bookingService.updateBookingStatus(bookingId, status);
        return ResponseEntity.noContent().build();
    }
}
