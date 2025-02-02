package com.flight.airline.airport;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AirportControllerTest {

    @Mock
    private AirportService airportService;

    @InjectMocks
    private AirportController airportController;

    private Airport testAirport;

    @BeforeEach
    void setUp() {
        testAirport = new Airport();
        testAirport.setId(1L);
        testAirport.setName("John F. Kennedy International Airport");
        testAirport.setCode("JFK");
        testAirport.setCity("New York");
        testAirport.setCountry("USA");
    }

    @Test
    void testGetAllAirports() {
        when(airportService.getAllAirports()).thenReturn(List.of(testAirport));

        ResponseEntity<List<Airport>> response = airportController.getAllAirports();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(airportService, times(1)).getAllAirports();
    }

    @Test
    void testGetAirportById_Found() {
        when(airportService.getAirportById(1L)).thenReturn(Optional.of(testAirport));

        ResponseEntity<Airport> response = airportController.getAirportById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAirport.getId(), response.getBody().getId());
        verify(airportService, times(1)).getAirportById(1L);
    }

    @Test
    void testGetAirportById_NotFound() {
        when(airportService.getAirportById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Airport> response = airportController.getAirportById(2L);

        assertEquals(404, response.getStatusCodeValue());
        verify(airportService, times(1)).getAirportById(2L);
    }

    @Test
    void testCreateAirport() {
        when(airportService.createAirport(any(Airport.class))).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.createAirport(testAirport);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAirport.getId(), response.getBody().getId());
        verify(airportService, times(1)).createAirport(any(Airport.class));
    }

    @Test
    void testUpdateAirport() {
        when(airportService.updateAirport(eq(1L), any(Airport.class))).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.updateAirport(1L, testAirport);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAirport.getId(), response.getBody().getId());
        verify(airportService, times(1)).updateAirport(eq(1L), any(Airport.class));
    }

    @Test
    void testDeleteAirport() {
        when(airportService.deleteAirport(1L)).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.deleteAirport(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAirport.getId(), response.getBody().getId());
        verify(airportService, times(1)).deleteAirport(1L);
    }
}

