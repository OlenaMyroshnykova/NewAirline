package com.flight.airline.booking;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(2)
public class BookingDataLoader implements CommandLineRunner {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public BookingDataLoader(BookingRepository bookingRepository, UserRepository userRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        if (bookingRepository.count() == 0) {
            User user = userRepository.findByUsername("user").orElseThrow();
            Flight flight1 = flightRepository.findById(1L).orElseThrow();
            Flight flight2 = flightRepository.findById(2L).orElseThrow();
            Flight flight3 = flightRepository.findById(3L).orElseThrow();

            Booking booking1 = new Booking();
            booking1.setUser(user);
            booking1.setFlight(flight1);
            booking1.setBookingTime(LocalDateTime.of(2025, 1, 29, 8, 0));
            booking1.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking1);

            Booking booking2 = new Booking();
            booking2.setUser(user);
            booking2.setFlight(flight2);
            booking2.setBookingTime(LocalDateTime.of(2025, 1, 29, 9, 0));
            booking2.setStatus(BookingStatus.PENDING);
            bookingRepository.save(booking2);

            Booking booking3 = new Booking();
            booking3.setUser(user);
            booking3.setFlight(flight3);
            booking3.setBookingTime(LocalDateTime.of(2025, 1, 29, 10, 0));
            booking3.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking3);
        }
    }
}

