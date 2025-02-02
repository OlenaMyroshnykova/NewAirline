package com.flight.airline.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flight.airline.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId")
    int countByFlightId(@Param("flightId") Long flightId);
    List<Booking> findByStatusAndBookingTimeBefore(BookingStatus status, LocalDateTime time);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    List<Booking> findByUser(User user);
}

