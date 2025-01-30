package com.flight.airline.airport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Optional<Airport> getAirportById(Long id) {
        return airportRepository.findById(id);
    }

    public Optional<Airport> getAirportByCode(String code) {
        return airportRepository.findByCode(code);
    }

    @Transactional
    public Airport createAirport(Airport airport) {
        if (airportRepository.findByCode(airport.getCode()).isPresent()) {
            throw new RuntimeException("Airport with code " + airport.getCode() + " already exists.");
        }
        return airportRepository.save(airport);
    }

    @Transactional
    public Airport updateAirport(Long id, Airport updatedAirport) {
        return airportRepository.findById(id)
            .map(airport -> {
                airport.setName(updatedAirport.getName());
                airport.setCode(updatedAirport.getCode());
                airport.setCity(updatedAirport.getCity());
                airport.setCountry(updatedAirport.getCountry());
                return airportRepository.save(airport);
            })
            .orElseThrow(() -> new RuntimeException("Airport not found with id " + id));
    }

    @Transactional
    public void deleteAirport(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new RuntimeException("Airport not found with id " + id);
        }
        airportRepository.deleteById(id);
    }
}

