package com.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Setter @Getter
@Entity
@Table(name = "vehicle_type")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle")
    private String vehicle;

    private Double fee;

    public Vehicle(String vehicle, Double fee) {
        this.vehicle = vehicle;
        this.fee = fee;
    }
}

