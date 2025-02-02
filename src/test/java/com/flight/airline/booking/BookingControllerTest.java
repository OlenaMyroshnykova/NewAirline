package com.flight.airline.booking;

import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingController bookingController;

    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void shouldReturnAllBookingsForAdmin() {
        List<Booking> bookings = List.of(booking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings();

        assertEquals(OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void shouldReturnUserBookingHistoryForAdmin() {
        List<Booking> bookings = List.of(booking);
        when(bookingService.getBookingsByUsername("testUser")).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getUserBookingHistory("testUser");

        assertEquals(OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void shouldReturnUserBookings() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser(user)).thenReturn(List.of(booking));

        ResponseEntity<?> response = bookingController.getUserBookings(authentication);

        assertEquals(OK, response.getStatusCode());
        assertEquals(List.of(booking), response.getBody());
    }

    @Test
    void shouldReturnNoBookingsMessageWhenUserHasNoBookings() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser(user)).thenReturn(List.of());

        ResponseEntity<?> response = bookingController.getUserBookings(authentication);

        assertEquals(OK, response.getStatusCode());
        assertEquals(Map.of("message", "No bookings found"), response.getBody());
    }

    @Test
    void shouldBookFlightForUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(bookingService.bookFlight("testUser", 1L)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.bookFlight(authentication, 1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void shouldConfirmBookingAsAdmin() {
        when(bookingService.confirmBooking(1L)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.confirmBooking(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void shouldCancelBookingAsAdmin() {
        when(bookingService.cancelBooking(1L)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.cancelBooking(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void shouldReturnUserBookingsByUserId() {
        List<Booking> bookings = List.of(booking);
        when(bookingService.getUserBookings(1L)).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getUserBookings(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void shouldReturnUserBookingsByStatus() {
        List<Booking> bookings = List.of(booking);
        when(bookingService.getUserBookingsByStatus(1L, BookingStatus.CONFIRMED)).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getUserBookingsByStatus(1L, BookingStatus.CONFIRMED);

        assertEquals(OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    // @Test
    // void shouldUpdateBookingStatus() {
    //     doNothing().when(bookingService).updateBookingStatus(1L, BookingStatus.CANCELED);

    //     ResponseEntity<Void> response = bookingController.updateBookingStatus(1L, BookingStatus.CANCELED);

    //     assertEquals(NO_CONTENT, response.getStatusCode());
    //     verify(bookingService, times(1)).updateBookingStatus(1L, BookingStatus.CANCELED);
    // }
}



