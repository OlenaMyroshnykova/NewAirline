package com.flight.airline.booking;

import com.flight.airline.flight.Flight;
import com.flight.airline.flight.FlightRepository;
import com.flight.airline.user.User;
import com.flight.airline.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingDataLoaderTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private BookingDataLoader bookingDataLoader;

    private User user;
    private Flight flight1;
    private Flight flight2;
    private Flight flight3;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");

        flight1 = new Flight();
        flight1.setId(1L);

        flight2 = new Flight();
        flight2.setId(2L);

        flight3 = new Flight();
        flight3.setId(3L);
    }

    @Test
    void shouldLoadBookingsWhenRepositoryIsEmpty() {
        when(bookingRepository.count()).thenReturn(0L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight1));
        when(flightRepository.findById(2L)).thenReturn(Optional.of(flight2));
        when(flightRepository.findById(3L)).thenReturn(Optional.of(flight3));

        bookingDataLoader.run();

        verify(bookingRepository, times(3)).save(any(Booking.class));
    }

    @Test
    void shouldNotLoadBookingsWhenRepositoryIsNotEmpty() {
        when(bookingRepository.count()).thenReturn(1L);

        bookingDataLoader.run();

        verifyNoInteractions(userRepository);
        verifyNoInteractions(flightRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(bookingRepository.count()).thenReturn(0L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> bookingDataLoader.run());

        verifyNoInteractions(flightRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionIfFlightNotFound() {
        when(bookingRepository.count()).thenReturn(0L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight1));
        when(flightRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> bookingDataLoader.run());

        verify(bookingRepository, never()).save(any(Booking.class));
    }
}

