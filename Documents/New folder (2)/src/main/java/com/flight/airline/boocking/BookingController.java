package com.flight.airline.boocking;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    public BookingController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    // Получить все бронирования пользователя (только сам пользователь или админ)
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public List<Booking> getUserBookings(Authentication auth) {
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(value -> bookingRepository.findByUserId(value.getId())).orElse(List.of());
    }

    // Создать новое бронирование (только для пользователей)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Booking> createBooking(Authentication auth, @RequestBody Booking booking) {
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            booking.setUser(user.get());
            booking.setBookingTime(LocalDateTime.now());
            return ResponseEntity.ok(bookingRepository.save(booking));
        }
        return ResponseEntity.badRequest().build();
    }
}

