package com.deliveryservice.service;

import com.deliveryservice.entity.City;
import com.deliveryservice.entity.Vehicle;
import com.deliveryservice.entity.WeatherData;
import com.deliveryservice.exceptions.CityNotFoundException;
import com.deliveryservice.exceptions.VehicleTypeNotFoundException;
import com.deliveryservice.exceptions.VehicleUsageForbiddenException;
import com.deliveryservice.repository.CityRepository;
import com.deliveryservice.repository.VehicleRepository;
import com.deliveryservice.repository.WeatherDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DeliveryService {
    CityRepository cityRepository;
    VehicleRepository vehicleRepository;
    WeatherDataRepository weatherDataRepository;

    /**
     * Calculates the delivery fee based on the provided cityName, vehicleType, and datetime.
     *
     * @param cityName    The name of the city for which the delivery fee will be calculated.
     * @param vehicleType The type of vehicle used for the delivery (e.g., "Scooter" or "Bike").
     * @param datetime    The datetime string in ISO 8601 format (e.g., "2022-12-01T15:30:00Z") for which the weather data will be considered.
     *                    If null, the latest weather data available will be used.
     * @return The calculated delivery fee as a double.
     * @throws CityNotFoundException          If the specified city is not found.
     * @throws VehicleTypeNotFoundException   If the specified vehicle type is not found.
     * @throws VehicleUsageForbiddenException If the usage of the specified vehicle type is forbidden due to weather conditions.
     */

    public double calculateDeliveryFee(String cityName, String vehicleType, String datetime) {
        Optional<WeatherData> latestWeatherDataOpt = findLatestWeatherData(cityName, datetime);

        if (latestWeatherDataOpt.isEmpty()) {
            throw new CityNotFoundException("No weather data found for the specified city and datetime");
        }

        WeatherData latestWeatherData = latestWeatherDataOpt.get();

        double rbf = calculateRegionalBaseFee(cityName, vehicleType);
        double atef = calculateAirTemperatureExtraFee(vehicleType, latestWeatherData.getAirTemperature());
        double wsef = calculateWindSpeedExtraFee(vehicleType, latestWeatherData.getWindSpeed());
        double wpef = calculateWeatherPhenomenonExtraFee(vehicleType, latestWeatherData.getWeatherPhenomenon());

        return rbf + atef + wsef + wpef;
    }

    private Optional<WeatherData> findLatestWeatherData(String cityName, String datetime) {
        if (datetime != null) {
            Instant latestTimestamp = Instant.parse(datetime);
            return weatherDataRepository
                    .findWeatherDataByStationNameAndTimestampOrderByTimestampDesc(cityName, latestTimestamp)
                    .stream()
                    .max(Comparator.comparing(WeatherData::getTimestamp));
        } else {
            return weatherDataRepository
                    .findWeatherDataByStationName(cityName)
                    .stream()
                    .findFirst();
        }
    }

    private double calculateRegionalBaseFee(String cityName, String vehicleType) {
        City city = cityRepository.findByCityIgnoreCase(cityName)
                .orElseThrow(() -> new CityNotFoundException("City not found"));
        Vehicle vehicle = vehicleRepository.findByVehicleIgnoreCase(vehicleType)
                .orElseThrow(() -> new VehicleTypeNotFoundException("No such vehicle type"));
        return city.getFee() + vehicle.getFee();
    }

    private double calculateAirTemperatureExtraFee(String vehicleType, Double airTemperature) {
        if (("Scooter".equalsIgnoreCase(vehicleType) || "Bike".equalsIgnoreCase(vehicleType))) {
            if (airTemperature < -10) {
                return 1.0;
            } else if (airTemperature >= -10 && airTemperature < 0) {
                return 0.5;
            }
        }
        return 0.0;
    }

    private double calculateWindSpeedExtraFee(String vehicleType, Double windSpeed) {
        if ("Bike".equalsIgnoreCase(vehicleType)) {
            if (windSpeed >= 10 && windSpeed <= 20) {
                return 0.5;
            } else if (windSpeed > 20) {
                throw new RuntimeException("Usage of selected vehicle type is forbidden");
            }
        }
        return 0.0;
    }
    private double calculateWeatherPhenomenonExtraFee(String vehicleType, String weatherPhenomenon) {
        if (vehicleType.equalsIgnoreCase("Scooter") || vehicleType.equalsIgnoreCase("Bike")) {
            if (isSnowOrSleet(weatherPhenomenon)) {
                return 1;
            } else if (isRain(weatherPhenomenon)) {
                return 0.5;
            } else if (isGlazeHailOrThunder(weatherPhenomenon)) {
                throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden");
            }
        }
        return 0;
    }

    private boolean isSnowOrSleet(String weatherPhenomenon) {
        List<String> snowOrSleetKeywords = Arrays.asList("snow", "sleet", "snowfall", "snow shower", "snow flurries");

        return snowOrSleetKeywords.stream().anyMatch(keyword -> weatherPhenomenon.toLowerCase().contains(keyword));
    }

    private boolean isRain(String weatherPhenomenon) {
        List<String> rainKeywords = Arrays.asList("rain", "drizzle", "rainfall", "rain shower", "light rain", "heavy rain");

        return rainKeywords.stream().anyMatch(keyword -> weatherPhenomenon.toLowerCase().contains(keyword));
    }

    private boolean isGlazeHailOrThunder(String weatherPhenomenon) {
        List<String> glazeHailOrThunderKeywords = Arrays.asList("glaze", "hail", "thunder", "thunderstorm");

        return glazeHailOrThunderKeywords.stream().anyMatch(keyword -> weatherPhenomenon.toLowerCase().contains(keyword));
    }
}