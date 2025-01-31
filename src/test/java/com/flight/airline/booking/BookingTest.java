package com.flight.airline.booking;


import com.flight.airline.flight.Flight;
import com.flight.airline.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        Flight flight = new Flight();
        flight.setId(1L);

        booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void testSaveBooking() {
        Booking savedBooking = bookingRepository.save(booking);
        assertNotNull(savedBooking.getId());
        assertEquals(booking.getUser().getId(), savedBooking.getUser().getId());
        assertEquals(booking.getFlight().getId(), savedBooking.getFlight().getId());
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
    }

    @Test
    void testFindById() {
        Booking savedBooking = bookingRepository.save(booking);
        Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());

        assertTrue(foundBooking.isPresent());
        assertEquals(savedBooking.getId(), foundBooking.get().getId());
    }
}

