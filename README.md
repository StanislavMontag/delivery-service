Delivery Service API Documentation
Introduction

This API provides a delivery service with various endpoints to manage fees and query delivery fee calculations based on city, vehicle type, and weather conditions.
Base URL

All requests to the API should be made to the following base URL:


Endpoints
1. Calculate Delivery Fee

Path: /delivery/feeRequest

Method: GET

Description: Calculates the delivery fee based on the provided city, vehicle type, and datetime (optional).

Request Parameters:

    city (string, required): The name of the city for which the delivery fee will be calculated.
    vehicleType (string, required): The type of vehicle used for the delivery (e.g., "Scooter" or "Bike").
    datetime (string, optional): The datetime string in ISO 8601 format (e.g., "2022-12-01T15:30:00Z") for which the weather data will be considered. If null, the latest weather data available will be used.

Response:

    200 OK: The calculated delivery fee as a double.

Example Request:

GET /delivery/feeRequest?city=Tallinn&vehicleType=Scooter

2. Set Cron Expression

Path: /delivery/cron

Method: POST

Description: Sets the cron expression for the weather data importer.

Request Parameters:

    cronExpression (string, required): The cron expression to set.

Response:

    200 OK: A success message with the new cron expression.
    400 Bad Request: An error message if the cron expression is not valid.

Example Request:


POST /delivery/cron?cronExpression=0%201%20*%20*%20*

3. Set City Fee

Path: /city/setFee

Method: POST

Description: Sets the fee for a specific city.

Request Parameters:

    cityName (string, required): The name of the city for which the fee will be updated.
    fee (double, required): The new fee for the city.

Response:

    200 OK: A success message with the updated city fee.
    404 Not Found: An error message if the specified city is not found.

Example Request:


POST /delivery/city/setFee?cityName=Tartu&fee=2.5

4. Set Vehicle Fee

Path: /vehicle/setFee

Method: POST

Description: Sets the fee for a specific vehicle type.

Request Parameters:

    vehicleType (string, required): The type of vehicle for which the fee will be updated (e.g., "Scooter" or "Bike").
    fee (double, required): The new fee for the vehicle type.

Response:

    200 OK: A success message with the updated vehicle fee.
    404 Not Found: An error message if the specified vehicle type is not found.

Example Request:


POST /delivery/vehicle/setFee?vehicleType=Bike&fee=1.5
