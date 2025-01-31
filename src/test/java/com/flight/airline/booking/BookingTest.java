package com.flight.airline.booking;

import com.flight.airline.flight.Flight;
import com.flight.airline.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private User user;
    private Flight flight;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        flight = new Flight();
        flight.setId(1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void testBookingCreation() {
        assertNotNull(booking);
    }

    @Test
    void testGetUser() {
        assertEquals(user, booking.getUser());
        assertEquals(1L, booking.getUser().getId());
    }

    @Test
    void testGetFlight() {
        assertEquals(flight, booking.getFlight());
        assertEquals(1L, booking.getFlight().getId());
    }

    @Test
    void testGetBookingTime() {
        assertNotNull(booking.getBookingTime());
    }

    @Test
    void testGetStatus() {
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }
}


