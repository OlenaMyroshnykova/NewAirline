package com.flight.airline.booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flight.airline.flight.Flight;
import com.flight.airline.user.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "authorities", "roles", "accountNonLocked", "accountNonExpired", "credentialsNonExpired", "enabled"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    private LocalDateTime bookingTime;
}

