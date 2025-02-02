package com.flight.airline.airport;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airports", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;
}
