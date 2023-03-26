package com.deliveryservice.exceptions;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(String message) {
        super(message);
    }
}