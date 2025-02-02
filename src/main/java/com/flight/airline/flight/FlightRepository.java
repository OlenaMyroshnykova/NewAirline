package com.flight.airline.flight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByOriginAirport_IdAndDestinationAirport_Id(Long originAirportId, Long destinationAirportId);
}

