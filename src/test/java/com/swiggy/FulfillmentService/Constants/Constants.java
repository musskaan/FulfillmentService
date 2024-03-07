package com.swiggy.FulfillmentService.Constants;

import com.swiggy.FulfillmentService.DTOs.*;

public class Constants {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "LastName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "Password123";
    public static final String PHONE = "1234567890";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP_CDE = "452001";
    public static final String ENCODED_PASSWORD = "encoded_password";
    public static final String SUCCESSFULLY_REGISTERED = "Delivery executive registered successfully";
    public static final String SUCCESSFULLY_ASSIGNED = "Delivery executive assigned successfully to the order";
    public static final Long ORDER_ID = 1L;
    public static final Location location = new Location(STREET, CITY, STATE, ZIP_CDE);
    public static final DeliveryExecutiveRegistrationRequest deliveryExecutiveRegistrationRequest = DeliveryExecutiveRegistrationRequest.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .phone(PHONE)
            .location(location)
            .build();

    public static final DeliveryExecutiveRegistrationResponse deliveryExecutiveRegistrationResponse = DeliveryExecutiveRegistrationResponse.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .phone(PHONE)
            .location(location)
            .message(SUCCESSFULLY_REGISTERED)
            .build();

    public static final DeliveryExecutiveDTO deliveryExecutiveDTO = new DeliveryExecutiveDTO(FIRST_NAME, LAST_NAME, PHONE);

    public static final DeliveryRequest deliveryRequest = new DeliveryRequest(ORDER_ID, location, location);

    public static final DeliveryResponse deliveryResponse = DeliveryResponse.builder()
            .orderId(ORDER_ID)
            .restaurantLocation(location)
            .customerLocation(location)
            .deliveryExecutive(deliveryExecutiveDTO)
            .message(SUCCESSFULLY_ASSIGNED)
            .build();
}
