package com.deliveryservice.service;

import com.deliveryservice.dto.DeliveryFeeResponse;
import com.deliveryservice.entity.City;
import com.deliveryservice.entity.Vehicle;
import com.deliveryservice.entity.WeatherData;
import com.deliveryservice.exceptions.CityNotFoundException;
import com.deliveryservice.exceptions.ResourceNotFoundException;
import com.deliveryservice.exceptions.VehicleTypeNotFoundException;
import com.deliveryservice.exceptions.VehicleUsageForbiddenException;
import com.deliveryservice.repository.CityRepository;
import com.deliveryservice.repository.VehicleRepository;
import com.deliveryservice.repository.WeatherDataRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    public DeliveryFeeResponse calculateDeliveryFee(String cityName, String vehicleType, String datetime) {
        Optional<WeatherData> latestWeatherDataOpt = findLatestWeatherData(cityName, datetime);

        if (latestWeatherDataOpt.isEmpty()) {
            throw new CityNotFoundException("No weather data found for the specified city and datetime");
        }

        WeatherData latestWeatherData = latestWeatherDataOpt.get();

        BigDecimal rbf = calculateRegionalBaseFee(cityName, vehicleType);
        BigDecimal atef = calculateAirTemperatureExtraFee(vehicleType, latestWeatherData.getAirTemperature());
        BigDecimal wsef = calculateWindSpeedExtraFee(vehicleType, latestWeatherData.getWindSpeed());
        BigDecimal wpef = calculateWeatherPhenomenonExtraFee(vehicleType, latestWeatherData.getWeatherPhenomenon());

        BigDecimal totalFee = rbf.add(atef).add(wsef).add(wpef);

        return new DeliveryFeeResponse(totalFee);
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

    private BigDecimal calculateRegionalBaseFee(String cityName, String vehicleType) {
        City city = cityRepository.findByCityIgnoreCase(cityName)
                .orElseThrow(() -> new CityNotFoundException("City not found"));
        Vehicle vehicle = vehicleRepository.findByVehicleIgnoreCase(vehicleType)
                .orElseThrow(() -> new VehicleTypeNotFoundException("No such vehicle type"));
        return BigDecimal.valueOf(city.getFee()).add(BigDecimal.valueOf(vehicle.getFee()));
    }

    private BigDecimal calculateAirTemperatureExtraFee(String vehicleType, Double airTemperature) {
        if (("Scooter".equalsIgnoreCase(vehicleType) || "Bike".equalsIgnoreCase(vehicleType))) {
            if (airTemperature < -10) {
                return BigDecimal.valueOf(1.0);
            } else if (airTemperature >= -10 && airTemperature < 0) {
                return BigDecimal.valueOf(0.5);
            }
        }
        return BigDecimal.valueOf(0.0);
    }

    private BigDecimal calculateWindSpeedExtraFee(String vehicleType, Double windSpeed) {
        if ("Bike".equalsIgnoreCase(vehicleType)) {
            if (windSpeed >= 10 && windSpeed <= 20) {
                return BigDecimal.valueOf(0.5);
            } else if (windSpeed > 20) {
                throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden");
            }
        }
        return BigDecimal.valueOf(0.0);
    }

    private BigDecimal calculateWeatherPhenomenonExtraFee(String vehicleType, String weatherPhenomenon) {
        if (vehicleType.equalsIgnoreCase("Scooter") || vehicleType.equalsIgnoreCase("Bike")) {
            if (isSnowOrSleet(weatherPhenomenon)) {
                return BigDecimal.valueOf(1);
            } else if (isRain(weatherPhenomenon)) {
                return BigDecimal.valueOf(0.5);
            } else if (isGlazeHailOrThunder(weatherPhenomenon)) {
                throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden");
            }
        }
        return BigDecimal.valueOf(0);
    }

    public void updateVehicleFee(String vehicleType, Double fee) {
        Vehicle vehicle = vehicleRepository.findByVehicleIgnoreCase(vehicleType)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleType));
        vehicle.setFee(fee);
        vehicleRepository.save(vehicle);
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