package com.flight.airline.flight;

import com.flight.airline.airport.Airport;
import com.flight.airline.airport.AirportRepository;
import com.flight.airline.booking.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllFlights_Success() {
        // Arrange
        Flight flight1 = new Flight();
        Flight flight2 = new Flight();
        when(flightRepository.findAll()).thenReturn(List.of(flight1, flight2));

        // Act
        List<Flight> flights = flightService.getAllFlights();

        // Assert
        assertEquals(2, flights.size());
        verify(flightRepository).findAll();
    }

    @Test
    void getFlightById_Success() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act
        Optional<Flight> result = flightService.getFlightById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(flightRepository).findById(1L);
    }

    @Test
    void getFlightById_NotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Flight> result = flightService.getFlightById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(flightRepository).findById(1L);
    }

    @Test
    void searchFlights_Success() {
        // Arrange
        Flight flight = new Flight();
        when(flightRepository.findByOriginAirport_IdAndDestinationAirport_Id(1L, 2L))
                .thenReturn(List.of(flight));

        // Act
        List<Flight> flights = flightService.searchFlights(1L, 2L);

        // Assert
        assertEquals(1, flights.size());
        verify(flightRepository).findByOriginAirport_IdAndDestinationAirport_Id(1L, 2L);
    }

    @Test
    void createFlight_Success() {
        // Arrange
        Airport origin = new Airport();
        origin.setId(1L);
        Airport destination = new Airport();
        destination.setId(2L);

        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(destination));

        Flight flight = new Flight();
        flight.setFlightNumber("AF123");
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // Act
        Flight createdFlight = flightService.createFlight("AF123", 1L, 2L, LocalDateTime.now(), 50);

        // Assert
        assertEquals("AF123", createdFlight.getFlightNumber());
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void createFlight_OriginNotFound() {
        when(airportRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            flightService.createFlight("AF123", 1L, 2L, LocalDateTime.now(), 50);
        });

        assertEquals("Origin airport not found", thrown.getMessage());
    }

    @Test
    void createFlight_DestinationNotFound() {
        Airport origin = new Airport();
        origin.setId(1L);

        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            flightService.createFlight("AF123", 1L, 2L, LocalDateTime.now(), 50);
        });

        assertEquals("Destination airport not found", thrown.getMessage());
    }

    @Test
    void updateFlight_Success() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);

        Airport origin = new Airport();
        origin.setId(1L);
        Airport destination = new Airport();
        destination.setId(2L);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // Act
        Flight updatedFlight = flightService.updateFlight(1L, "AF123", 1L, 2L, LocalDateTime.now(), 30);

        // Assert
        assertEquals(1L, updatedFlight.getId());
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_NotFound() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            flightService.updateFlight(1L, "AF123", 1L, 2L, LocalDateTime.now(), 30);
        });

        assertEquals("Flight not found", thrown.getMessage());
    }

    @Test
    void deleteFlight_Success() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act
        Flight deletedFlight = flightService.deleteFlight(1L);

        // Assert
        assertEquals(1L, deletedFlight.getId());
        verify(flightRepository).delete(flight);
    }

    @Test
    void deleteFlight_NotFound() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            flightService.deleteFlight(1L);
        });

        assertEquals("Flight not found", thrown.getMessage());
    }

    @Test
    void updateFlightAvailability_Success() {
        // Arrange
        Flight flight1 = new Flight();
        flight1.setId(1L);
        flight1.setAvailableSeats(50);
        flight1.setDepartureTime(LocalDateTime.now().plusDays(1));

        Flight flight2 = new Flight();
        flight2.setId(2L);
        flight2.setAvailableSeats(0);
        flight2.setDepartureTime(LocalDateTime.now().minusDays(1));

        when(flightRepository.findAll()).thenReturn(List.of(flight1, flight2));
        when(bookingRepository.countByFlightId(1L)).thenReturn(10);
        when(bookingRepository.countByFlightId(2L)).thenReturn(5);

        // Act
        flightService.updateFlightAvailability();

        // Assert
        assertTrue(flight1.getAvailable()); // Доступен
        assertFalse(flight2.getAvailable()); // Недоступен

        verify(flightRepository, times(2)).save(any(Flight.class));
    }
}

