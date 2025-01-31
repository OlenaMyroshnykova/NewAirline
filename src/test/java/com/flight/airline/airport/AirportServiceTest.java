package com.flight.airline.airport;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportService airportService;

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
        when(airportRepository.findAll()).thenReturn(List.of(testAirport));

        List<Airport> airports = airportService.getAllAirports();

        assertEquals(1, airports.size());
        assertEquals(testAirport.getCode(), airports.get(0).getCode());
        verify(airportRepository, times(1)).findAll();
    }

    @Test
    void testGetAirportById_Found() {
        when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));

        Optional<Airport> airport = airportService.getAirportById(1L);

        assertTrue(airport.isPresent());
        assertEquals("JFK", airport.get().getCode());
        verify(airportRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAirportById_NotFound() {
        when(airportRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Airport> airport = airportService.getAirportById(2L);

        assertFalse(airport.isPresent());
        verify(airportRepository, times(1)).findById(2L);
    }

    @Test
    void testGetAirportByCode_Found() {
        when(airportRepository.findByCode("JFK")).thenReturn(Optional.of(testAirport));

        Optional<Airport> airport = airportService.getAirportByCode("JFK");

        assertTrue(airport.isPresent());
        assertEquals("JFK", airport.get().getCode());
        verify(airportRepository, times(1)).findByCode("JFK");
    }

    @Test
    void testCreateAirport_Success() {
        when(airportRepository.findByCode("JFK")).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class))).thenReturn(testAirport);

        Airport savedAirport = airportService.createAirport(testAirport);

        assertNotNull(savedAirport);
        assertEquals("JFK", savedAirport.getCode());
        verify(airportRepository, times(1)).findByCode("JFK");
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void testCreateAirport_Fail_AlreadyExists() {
        when(airportRepository.findByCode("JFK")).thenReturn(Optional.of(testAirport));

        Exception exception = assertThrows(RuntimeException.class, () -> airportService.createAirport(testAirport));

        assertEquals("Airport with code JFK already exists.", exception.getMessage());
        verify(airportRepository, times(1)).findByCode("JFK");
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void testUpdateAirport_Success() {
        Airport updatedAirport = new Airport();
        updatedAirport.setName("Updated Airport");
        updatedAirport.setCode("JFK");
        updatedAirport.setCity("Updated City");
        updatedAirport.setCountry("Updated Country");

        when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(any(Airport.class))).thenReturn(updatedAirport);

        Airport result = airportService.updateAirport(1L, updatedAirport);

        assertEquals("Updated Airport", result.getName());
        assertEquals("Updated City", result.getCity());
        assertEquals("Updated Country", result.getCountry());
        verify(airportRepository, times(1)).findById(1L);
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void testUpdateAirport_NotFound() {
        when(airportRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> airportService.updateAirport(2L, testAirport));

        assertEquals("Airport not found with id 2", exception.getMessage());
        verify(airportRepository, times(1)).findById(2L);
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void testDeleteAirport_Success() {
        when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));

        Airport deletedAirport = airportService.deleteAirport(1L);

        assertNotNull(deletedAirport);
        assertEquals(1L, deletedAirport.getId());
        verify(airportRepository, times(1)).findById(1L);
        verify(airportRepository, times(1)).delete(testAirport);
    }

    @Test
    void testDeleteAirport_NotFound() {
        when(airportRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> airportService.deleteAirport(2L));

        assertEquals("Airport not found", exception.getMessage());
        verify(airportRepository, times(1)).findById(2L);
        verify(airportRepository, never()).delete(any(Airport.class));
    }
}

