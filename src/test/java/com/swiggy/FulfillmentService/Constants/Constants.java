package com.swiggy.FulfillmentService.Constants;

import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationRequest;
import com.swiggy.FulfillmentService.DTOs.Location;

public class Constants {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "LastName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP_CDE = "zip";
    public static final String ENCODED_PASSWORD = "encoded_password";
    public static final String SUCCESSFULLY_REGISTERED = "Delivery executive registered successfully";
    public static final Location location = new Location(STREET, CITY, STATE, ZIP_CDE);
    public static final DeliveryExecutiveRegistrationRequest deliveryExecutiveRegistrationRequest = DeliveryExecutiveRegistrationRequest.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .phone(PHONE)
            .location(location)
            .build();
}
