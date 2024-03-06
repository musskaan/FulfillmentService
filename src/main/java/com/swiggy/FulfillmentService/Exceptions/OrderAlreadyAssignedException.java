package com.swiggy.FulfillmentService.Exceptions;

public class OrderAlreadyAssignedException extends RuntimeException {
    public OrderAlreadyAssignedException(String message) {
        super(message);
    }
}
