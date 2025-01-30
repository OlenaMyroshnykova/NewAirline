package com.flight.airline.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/users/{userId}/book/{flightId}")
    public ResponseEntity<Booking> bookFlight(@PathVariable Long userId, @PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.bookFlight(userId, flightId));
    }

    @PutMapping("/admin/bookings/{bookingId}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long bookingId) {
        bookingService.confirmBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/bookings/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // 📌 Получить бронирования конкретного пользователя
    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    // 📌 Получить бронирования пользователя по статусу
    @GetMapping("/users/{userId}/bookings/status/{status}")
    public ResponseEntity<List<Booking>> getUserBookingsByStatus(@PathVariable Long userId, @PathVariable BookingStatus status) {
        return ResponseEntity.ok(bookingService.getUserBookingsByStatus(userId, status));
    }

    // 📌 Обновить статус бронирования (для админа)
    @PutMapping("/admin/bookings/{bookingId}/status/{status}")
    public ResponseEntity<Void> updateBookingStatus(@PathVariable Long bookingId, @PathVariable BookingStatus status) {
        bookingService.updateBookingStatus(bookingId, status);
        return ResponseEntity.noContent().build();
    }
}


