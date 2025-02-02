package com.flight.airline.airport;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
    void getAllAirports_ReturnsListOfAirports() {
        when(airportService.getAllAirports()).thenReturn(List.of(testAirport));

        ResponseEntity<List<Airport>> response = airportController.getAllAirports();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(airportService).getAllAirports();
    }

    @Test
    void getAirportById_WhenExists_ReturnsAirport() {
        when(airportService.getAirportById(1L)).thenReturn(Optional.of(testAirport));

        ResponseEntity<Airport> response = airportController.getAirportById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().extracting(Airport::getId).isEqualTo(1L);
        verify(airportService).getAirportById(1L);
    }

    @Test
    void getAirportById_WhenNotExists_ReturnsNotFound() {
        when(airportService.getAirportById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Airport> response = airportController.getAirportById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(airportService).getAirportById(2L);
    }

    @Test
    void createAirport_SuccessfullyCreatesAirport() {
        when(airportService.createAirport(any(Airport.class))).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.createAirport(testAirport);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().extracting(Airport::getId).isEqualTo(1L);
        verify(airportService).createAirport(any(Airport.class));
    }

    @Test
    void updateAirport_SuccessfullyUpdatesAirport() {
        when(airportService.updateAirport(eq(1L), any(Airport.class))).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.updateAirport(1L, testAirport);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().extracting(Airport::getId).isEqualTo(1L);
        verify(airportService).updateAirport(eq(1L), any(Airport.class));
    }

    @Test
    void deleteAirport_SuccessfullyDeletesAirport() {
        when(airportService.deleteAirport(1L)).thenReturn(testAirport);

        ResponseEntity<Airport> response = airportController.deleteAirport(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().extracting(Airport::getId).isEqualTo(1L);
        verify(airportService).deleteAirport(1L);
    }
}


