package com.flight.airline.flight;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.airline.airport.Airport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    private Flight testFlight;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightController).build();

        objectMapper.registerModule(new JavaTimeModule());

        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setFlightNumber("AF123");
        testFlight.setDepartureTime(LocalDateTime.of(2025, 2, 1, 13, 0));
        testFlight.setAvailableSeats(10);

        Airport originAirport = new Airport();
        originAirport.setId(1L);
        originAirport.setName("JFK");
        originAirport.setCode("JFK");

        Airport destinationAirport = new Airport();
        destinationAirport.setId(2L);
        destinationAirport.setName("LHR");
        destinationAirport.setCode("LHR");

        testFlight.setOriginAirport(originAirport);
        testFlight.setDestinationAirport(destinationAirport);
    }

    @Test
    void testGetAllFlights() throws Exception {
        when(flightService.getAllFlights()).thenReturn(List.of(testFlight));

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("AF123"));

        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    void testGetFlightById_Found() throws Exception {
        when(flightService.getFlightById(1L)).thenReturn(Optional.of(testFlight));

        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AF123"));

        verify(flightService, times(1)).getFlightById(1L);
    }

    @Test
    void testGetFlightById_NotFound() throws Exception {
        when(flightService.getFlightById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/flights/2"))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).getFlightById(2L);
    }

    @Test
    void testSearchFlights() throws Exception {
        when(flightService.searchFlights(1L, 2L)).thenReturn(List.of(testFlight));

        mockMvc.perform(get("/api/flights/search")
                        .param("originAirportId", "1")
                        .param("destinationAirportId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("AF123"));

        verify(flightService, times(1)).searchFlights(1L, 2L);
    }

    @Test
    void testCreateFlight() throws Exception {
        when(flightService.createFlight(
                anyString(),
                anyLong(),
                anyLong(),
                any(LocalDateTime.class),
                anyInt()
        )).thenReturn(testFlight);

        mockMvc.perform(post("/api/admin/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFlight)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AF123"));

        verify(flightService, times(1)).createFlight(
                anyString(), anyLong(), anyLong(), any(LocalDateTime.class), anyInt()
        );
    }

    @Test
    void testUpdateFlight() throws Exception {
        when(flightService.updateFlight(
                anyLong(),
                anyString(),
                anyLong(),
                anyLong(),
                any(LocalDateTime.class),
                anyInt()
        )).thenReturn(testFlight);

        mockMvc.perform(put("/api/admin/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFlight)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AF123"));

        verify(flightService, times(1)).updateFlight(
                anyLong(), anyString(), anyLong(), anyLong(), any(LocalDateTime.class), anyInt()
        );
    }

    @Test
    void testDeleteFlight() throws Exception {
        when(flightService.deleteFlight(1L)).thenReturn(testFlight);

        mockMvc.perform(delete("/api/admin/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AF123"));

        verify(flightService, times(1)).deleteFlight(1L);
    }
}

