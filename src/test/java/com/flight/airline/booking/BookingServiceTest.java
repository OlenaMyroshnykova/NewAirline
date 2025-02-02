package com.flight.airline.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Flight testFlight;
    private User testUser;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setFlightNumber("AF123");
        testFlight.setAvailableSeats(10);
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setAvailable(true);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setFlight(testFlight);
        testBooking.setBookingTime(LocalDateTime.now());
        testBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    void testGetAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getAllBookings();

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testBookFlight_Success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking booking = bookingService.bookFlight("testuser", 1L);

        assertNotNull(booking);
        assertEquals(testUser, booking.getUser());
        assertEquals(testFlight, booking.getFlight());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals(9, testFlight.getAvailableSeats());
        verify(flightRepository, times(1)).save(testFlight);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testBookFlight_NoSeatsAvailable() {
        
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAvailableSeats(0);
        flight.updateAvailability();

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            bookingService.bookFlight("testUser", 1L);
        });

        assertEquals("No available seats", thrown.getMessage());
    }

    @Test
    void testConfirmBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        Booking confirmedBooking = bookingService.confirmBooking(1L);

        assertEquals(BookingStatus.CONFIRMED, confirmedBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testCancelBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        Booking canceledBooking = bookingService.cancelBooking(1L);

        assertEquals(11, testFlight.getAvailableSeats());
        verify(bookingRepository, times(1)).delete(testBooking);
        verify(flightRepository, times(1)).save(testFlight);
    }

    @Test
    void testGetUserBookings() {
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getUserBookings(1L);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetUserBookingsByStatus() {
        when(bookingRepository.findByUserIdAndStatus(1L, BookingStatus.PENDING)).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getUserBookingsByStatus(1L, BookingStatus.PENDING);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByUserIdAndStatus(1L, BookingStatus.PENDING);
    }

    @Test
    void testUpdateBookingStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED);

        assertEquals(BookingStatus.CONFIRMED, testBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testCleanupExpiredBookings() {
        Flight testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setFlightNumber("AF123");
        testFlight.setDepartureTime(LocalDateTime.now().plusHours(2)); // Устанавливаем корректное время

        Booking expiredBooking = new Booking();
        expiredBooking.setId(1L);
        expiredBooking.setBookingTime(LocalDateTime.now().minusMinutes(20)); // Просроченное бронирование
        expiredBooking.setStatus(BookingStatus.PENDING);
        expiredBooking.setFlight(testFlight);

        when(bookingRepository.findByStatusAndBookingTimeBefore(eq(BookingStatus.PENDING), any()))
            .thenReturn(List.of(expiredBooking));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(expiredBooking));

        bookingService.cleanupExpiredBookings();

        verify(bookingRepository, times(1)).delete(expiredBooking);
    }

    @Test
    void testGetBookingsByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByUser(testUser)).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getBookingsByUsername("testuser");

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByUser(testUser);
    }
}




