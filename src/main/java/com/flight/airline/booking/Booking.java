package com.flight.airline.booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "flight_id", nullable = false, foreignKey = @ForeignKey(name = "FK_flight_booking", value = ConstraintMode.CONSTRAINT, foreignKeyDefinition = "FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE"))
    @JsonIgnoreProperties({"bookings"})
    private Flight flight;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}

