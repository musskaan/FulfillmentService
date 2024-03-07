package com.swiggy.FulfillmentService.Exceptions;

public class UnauthorizedStatusUpdateException extends RuntimeException {
    public UnauthorizedStatusUpdateException(String message) {
        super(message);
    }
}
