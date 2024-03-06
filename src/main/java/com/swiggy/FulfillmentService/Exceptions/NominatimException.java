package com.swiggy.FulfillmentService.Exceptions;

public class NominatimException extends RuntimeException {
    public NominatimException(String message) {
        super(message);
    }
}
