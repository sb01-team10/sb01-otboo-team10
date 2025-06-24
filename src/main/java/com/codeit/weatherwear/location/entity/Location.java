package com.codeit.weatherwear.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private UUID id;

    private double latitude;

    private double longitude;

    private int x;

    private int y;

    private String name;

    public Location(double latitude, double longitude, int x, int y, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = x;
        this.y = y;
        this.name = name;
    }
}
