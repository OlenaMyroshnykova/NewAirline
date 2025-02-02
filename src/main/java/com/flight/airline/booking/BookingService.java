package com.flight.airline.booking;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking bookFlight(String userName, Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (flight.getAvailable() == false) {
            throw new RuntimeException("No available seats");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flight.updateAvailability();

        flightRepository.save(flight);
        return bookingRepository.save(booking);
    }
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }
        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    
        Flight flight = booking.getFlight();
    
        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        flight.updateAvailability();
    
        bookingRepository.delete(booking);
        flightRepository.save(flight);
        return booking;
    }
    
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getUserBookingsByStatus(Long userId, BookingStatus status) {
        return bookingRepository.findByUserIdAndStatus(userId, status);
    }

        @Transactional
    public void updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(newStatus);
        bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndBookingTimeBefore(BookingStatus.PENDING, cutoffTime);

        for (Booking booking : expiredBookings) {
            cancelBooking(booking.getId());
        }
    }

    public List<Booking> getBookingsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUser(user);
    }
}

