package com.flight.airline.flight;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/flights/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flights/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam Long originAirportId,
            @RequestParam Long destinationAirportId) {
        return ResponseEntity.ok(flightService.searchFlights(originAirportId, destinationAirportId));
    }

    @PostMapping("/admin/flights")
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.createFlight(
                flight.getFlightNumber(),
                flight.getOriginAirport().getId(),
                flight.getDestinationAirport().getId(),
                flight.getDepartureTime(),
                flight.getAvailableSeats()
        ));
    }

    @PutMapping("/admin/flights/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.updateFlight(
                id,
                flight.getFlightNumber(),
                flight.getOriginAirport().getId(),
                flight.getDestinationAirport().getId(),
                flight.getDepartureTime(),
                flight.getAvailableSeats()
        ));
    }

    @DeleteMapping("/admin/flights/{id}")
    public ResponseEntity<Flight> deleteFlight(@PathVariable Long id) {
        Flight deletedFlight = flightService.deleteFlight(id);
        return ResponseEntity.ok(deletedFlight);
    }}


