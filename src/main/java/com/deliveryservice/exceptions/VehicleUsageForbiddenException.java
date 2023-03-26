package com.deliveryservice.exceptions;

public class VehicleUsageForbiddenException extends RuntimeException {
    public VehicleUsageForbiddenException(String message) {
        super(message);
    }
}