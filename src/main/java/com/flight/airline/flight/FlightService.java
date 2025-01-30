package com.flight.airline.flight;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flight.airline.airport.Airport;
import com.flight.airline.airport.AirportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public List<Flight> searchFlights(Long originAirportId, Long destinationAirportId) {
        return flightRepository.findByOriginAirport_IdAndDestinationAirport_Id(originAirportId, destinationAirportId);
    }

    @Transactional
    public Flight createFlight(String flightNumber, Long originAirportId, Long destinationAirportId, LocalDateTime departureTime, int availableSeats) {
        Airport originAirport = airportRepository.findById(originAirportId)
                .orElseThrow(() -> new RuntimeException("Origin airport not found"));
        Airport destinationAirport = airportRepository.findById(destinationAirportId)
                .orElseThrow(() -> new RuntimeException("Destination airport not found"));

        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setOriginAirport(originAirport);
        flight.setDestinationAirport(destinationAirport);
        flight.setDepartureTime(departureTime);
        flight.setAvailableSeats(availableSeats);

        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlight(Long id, String flightNumber, Long originAirportId, Long destinationAirportId, LocalDateTime departureTime, int availableSeats) {
        return flightRepository.findById(id)
            .map(flight -> {
                Airport originAirport = airportRepository.findById(originAirportId)
                        .orElseThrow(() -> new RuntimeException("Origin airport not found"));
                Airport destinationAirport = airportRepository.findById(destinationAirportId)
                        .orElseThrow(() -> new RuntimeException("Destination airport not found"));

                flight.setFlightNumber(flightNumber);
                flight.setOriginAirport(originAirport);
                flight.setDestinationAirport(destinationAirport);
                flight.setDepartureTime(departureTime);
                flight.setAvailableSeats(availableSeats);

                return flightRepository.save(flight);
            })
            .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    @Transactional
    public Flight deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Flight not found"));
        
        flightRepository.delete(flight);
        return flight;
    }

    @Transactional
    public void checkAndUpdateFlightsAvailability() {
        List<Flight> flights = flightRepository.findAll();
        flights.forEach(flight -> {
            boolean previousAvailability = flight.isAvailable();
            flight.updateAvailability();
            if (flight.isAvailable() != previousAvailability) {
                flightRepository.save(flight);
            }
        });
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledAvailabilityCheck() {
        checkAndUpdateFlightsAvailability();
    }
}


