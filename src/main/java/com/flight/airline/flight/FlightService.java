package com.flight.airline.flight;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flight.airline.airport.Airport;
import com.flight.airline.airport.AirportRepository;
import com.flight.airline.booking.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final BookingRepository bookingRepository;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.bookingRepository = bookingRepository;
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

    @Scheduled(fixedRate = 60000) // Запуск каждые 60 секунд (1 минута)
    @Transactional
    public void updateFlightAvailability() {
        List<Flight> flights = flightRepository.findAll();

        for (Flight flight : flights) {
            int bookedSeats = bookingRepository.countByFlightId(flight.getId());
            int availableSeats = flight.getAvailableSeats() - bookedSeats; // Вычисляем свободные места

            boolean isFlightExpired = flight.getDepartureTime().isBefore(LocalDateTime.now()); // Проверяем, истекло ли время
            boolean isAvailable = availableSeats > 0 && !isFlightExpired;
            
            flight.setAvailable(isAvailable);
            //flight.setAvailableSeats(Math.max(availableSeats, 0)); // Чтобы не уходило в минус

            flightRepository.save(flight);
        }

    }
}


