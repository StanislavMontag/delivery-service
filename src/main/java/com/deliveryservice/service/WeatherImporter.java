package com.deliveryservice.service;

import com.deliveryservice.entity.WeatherData;
import com.deliveryservice.repository.WeatherDataRepository;
import jakarta.annotation.PostConstruct;
import org.quartz.CronExpression;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import lombok.extern.slf4j.Slf4j;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class WeatherImporter {
    private final WeatherDataRepository weatherDataRepository;
    private final TaskScheduler taskScheduler;
    private String cronExpression = "0 15 * * * *";
    private ScheduledFuture<?> scheduledTask;
    private static final Logger logger = LoggerFactory.getLogger(WeatherImporter.class);

    @PostConstruct
    // Fetching data right after application start
    public void init() {
        try {
            List<WeatherData> newWeatherData = requestWeatherData();
            weatherDataRepository.saveAll(newWeatherData);
        } catch (Exception e) {
            logger.debug("Error importing weather data", e);
        }
        startImportingWeatherData();
    }
    public WeatherImporter(WeatherDataRepository weatherDataRepository, TaskScheduler taskScheduler) {
        this.weatherDataRepository = weatherDataRepository;
        this.taskScheduler = taskScheduler;
    }
    private static final String API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private static final String[] STATIONS = {"Tallinn-Harku", "Tartu-Tõravere", "Pärnu"};

    public void startImportingWeatherData() {
        if (scheduledTask == null) {
            scheduledTask = taskScheduler.schedule(() -> {
                try {
                    List<WeatherData> newWeatherData = requestWeatherData();
                    weatherDataRepository.saveAll(newWeatherData);
                } catch (Exception e) {
                    logger.debug("Error importing weather data", e);
                }
            }, new CronTrigger(cronExpression));
        }
    }

    public void stopImportingWeatherData() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
        }
    }

    public void setCronExpression(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }
        this.cronExpression = cronExpression;
        stopImportingWeatherData();
        startImportingWeatherData();
    }

    public List<WeatherData> requestWeatherData() throws Exception {
        List<WeatherData> weatherDataList = new ArrayList<>();
        // Create the HTTP connection and set the request method to GET
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check if the response code indicates a successful response (200)
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Create a new DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the response into a Document object
            Document doc = builder.parse(connection.getInputStream());

            // Loop through the "station" elements in the XML response
            NodeList stationList = doc.getElementsByTagName("station");
            for (int i = 0; i < stationList.getLength(); i++) {
                Element station = (Element) stationList.item(i);
                String name = station.getElementsByTagName("name").item(0).getTextContent();
                if (containsStation(name)) {
                    // Create a new WeatherData object and set its properties
                    WeatherData weatherData = new WeatherData();
                    weatherData.setStationName(name);
                    weatherData.setWmoCode(station.getElementsByTagName("wmocode").item(0).getTextContent());
                    weatherData.setAirTemperature(Double.valueOf(station.getElementsByTagName("airtemperature").item(0).getTextContent()));
                    weatherData.setWindSpeed(Double.valueOf(station.getElementsByTagName("windspeed").item(0).getTextContent()));
                    weatherData.setWeatherPhenomenon(station.getElementsByTagName("phenomenon").item(0).getTextContent());
                    weatherData.setTimestamp(Instant.ofEpochSecond(Long.parseLong(doc.getDocumentElement().getAttribute("timestamp"))));
                    weatherDataList.add(weatherData);
                }
            }
        }

        // Close the connection and return the list of WeatherData objects
        connection.disconnect();
        return weatherDataList;
    }
    // Helper method to check if a station is in the list of allowed stations
    private static boolean containsStation(String stationName) {
        for (String allowedStation : STATIONS) {
            if (allowedStation.equalsIgnoreCase(stationName)) {
                return true;
            }
        }
        return false;
    }
}