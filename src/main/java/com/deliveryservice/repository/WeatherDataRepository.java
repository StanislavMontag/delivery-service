package com.deliveryservice.repository;

import com.deliveryservice.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findWeatherDataByStationNameAndTimestampOrderByTimestampDesc(String city, Instant latestTimestamp);
    Optional<WeatherData> findWeatherDataByStationName(String city);
}