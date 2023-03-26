# Delivery Service API

This repository contains the source code for a delivery service API. It provides endpoints for calculating delivery fees based on city, vehicle type, and weather conditions. It also allows setting cron expressions for weather importing and updating fees for cities and vehicles.

## Table of Contents

- [API Endpoints](#api-endpoints)
- [Usage](#usage)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [P.S.](#P.S.)

## API Endpoints

| Method | Endpoint                    | Description                                                  | Request Params          | Example                           |
|--------|-----------------------------|--------------------------------------------------------------|-------------------------|-----------------------------------|
| GET    | /delivery/feeRequest        | Calculate the delivery fee for a given city and vehicle type | city, vehicleType, datetime (optional) | /delivery/feeRequest?city=Tallinn&vehicleType=Scooter&datetime=2023-03-26T15:30:00Z |
| POST   | /delivery/cron              | Set the cron expression for weather importing                | cronExpression          | /delivery/cron?cronExpression=0 0 * * *            |
| POST   | /delivery/city/setFee       | Set the base fee for a city                                  | cityName, fee           | /delivery/city/setFee?cityName=Tallinn&fee=5.0        |
| POST   | /delivery/vehicle/setFee    | Set the base fee for a vehicle type                          | vehicleType, fee        | /delivery/vehicle/setFee?vehicleType=Scooter&fee=2.0  |

## Usage

The delivery service API allows users to calculate the delivery fee for a given city and vehicle type, considering the current weather conditions. The API also provides endpoints for setting cron expressions for weather importing and updating fees for cities and vehicles.

## Prerequisites

Before you can install and run the application, ensure that you have the following installed on your system:

- Java 11 or higher
- Maven

## Installation

1. Clone the repository to your local machine:

git clone https://github.com/ugress/DeliveryService.git

2. Navigate to the project directory:

cd DeliveryService

3. Install the project dependencies:

mvn install

4. Build the project:

mvn clean package

## Running the Application:

java -jar target/DeliveryService-0.0.1-SNAPSHOT.jar

## P.S.

I always try to stick to the best practices,
so there's Springdoc OpenAI available on: /swagger-ui/index.html for convenience, but it not well documented yet!
