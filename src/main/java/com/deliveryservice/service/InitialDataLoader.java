package com.deliveryservice.service;

import com.deliveryservice.entity.City;
import com.deliveryservice.entity.Vehicle;
import com.deliveryservice.repository.CityRepository;
import com.deliveryservice.repository.VehicleRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class InitialDataLoader {

    CityRepository cityRepository;
    VehicleRepository vehicleRepository;

    @PostConstruct
    public void loadInitialData() {
        City city1 = new City();
        city1.setCity("Tallinn-Harku");
        city1.setFee(2.5);
        cityRepository.save(city1);

        City city2 = new City();
        city2.setCity("Tartu-Tõravere");
        city2.setFee(2.0);
        cityRepository.save(city2);

        City city3 = new City();
        city3.setCity("Pärnu");
        city3.setFee(1.5);
        cityRepository.save(city3);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicle("car");
        vehicle1.setFee(1.5);
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicle("scooter");
        vehicle2.setFee(1.0);
        vehicleRepository.save(vehicle2);

        Vehicle vehicle3 = new Vehicle();
        vehicle3.setVehicle("bike");
        vehicle3.setFee(0.5);
        vehicleRepository.save(vehicle3);
    }
}
