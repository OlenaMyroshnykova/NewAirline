package com.flight.airline.flight;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.flight.airline.airport.Airport;
import com.flight.airline.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "origin_airport_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Airport originAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Airport destinationAirport;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int availableSeats;

    @Column(nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    public Flight(String flightNumber, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime, int availableSeats, boolean available) {
        this.flightNumber = flightNumber;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.available = available;
    }

    public void updateAvailability() {
        if (departureTime == null) {
            this.available = false;
            return;
        }
        this.available = availableSeats > 0 && departureTime.isAfter(LocalDateTime.now());
    }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
        updateAvailability();
    }

    public void setOriginAirport(Airport originAirport) {
        this.originAirport = originAirport;
    }

    public void setDestinationAirport(Airport destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public boolean getAvailable() {
        return available; 
    }
}



