package com.flight.airline.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.airline.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusAndBookingTimeBefore(BookingStatus status, LocalDateTime time);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    List<Booking> findByUser(User user);
}
