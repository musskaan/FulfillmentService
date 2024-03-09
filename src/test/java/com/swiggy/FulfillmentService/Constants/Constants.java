package com.swiggy.FulfillmentService.Constants;

import com.swiggy.FulfillmentService.DTOs.*;
import com.swiggy.FulfillmentService.Entities.Delivery;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Enums.Availability;
import com.swiggy.FulfillmentService.Enums.DeliveryStatus;

public class Constants {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "LastName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "Password123";
    public static final String INVALID_PASSWORD = "invalid";
    public static final String PHONE = "1234567890";
    public static final String INVALID_PHONE_HAVING_LETTERS = "abc";
    public static final String INVALID_PHONE_HAVING_LESS_DIGITS = "1234";
    public static final String INVALID_PHONE_HAVING_MORE_DIGITS = "1234";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP_CODE = "452001";
    public static final String INVALID_ZIP_CODE = "invalid";
    public static final String ANOTHER_ZIP_CODE = "412308";
    public static final String ENCODED_PASSWORD = "encoded_password";
    public static final String SUCCESSFULLY_REGISTERED = "Delivery executive registered successfully";
    public static final String SUCCESSFULLY_ASSIGNED = "Delivery executive assigned successfully";
    public static final String DELIVERY_ID = "54321";
    public static final Long ORDER_ID = 1L;
    public static final Address address = new Address(STREET, CITY, STATE, ZIP_CODE);
    public static final DeliveryExecutiveRegistrationRequest deliveryExecutiveRegistrationRequest = DeliveryExecutiveRegistrationRequest.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .phone(PHONE)
            .address(address)
            .build();

    public static final DeliveryExecutiveRegistrationResponse deliveryExecutiveRegistrationResponse = DeliveryExecutiveRegistrationResponse.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .phone(PHONE)
            .address(address)
            .message(SUCCESSFULLY_REGISTERED)
            .build();

    public static final DeliveryExecutiveDTO deliveryExecutiveDTO = new DeliveryExecutiveDTO(FIRST_NAME, LAST_NAME, PHONE);

    public static final DeliveryRequest deliveryRequest = new DeliveryRequest(ORDER_ID, address, address);

    public static final DeliveryResponse deliveryResponse = DeliveryResponse.builder()
            .orderId(ORDER_ID)
            .restaurantAddress(address)
            .customerAddress(address)
            .deliveryExecutive(deliveryExecutiveDTO)
            .message(SUCCESSFULLY_ASSIGNED)
            .build();

    public static final DeliveryExecutive availableDeliveryExecutive = DeliveryExecutive.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .address(address)
            .phone(PHONE)
            .availability(Availability.AVAILABLE)
            .build();

    public static final DeliveryExecutive unavailableDeliveryExecutive = DeliveryExecutive.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .address(address)
            .phone(PHONE)
            .availability(Availability.UNAVAILABLE)
            .build();

    public static final Delivery delivery = Delivery.builder()
            .id(DELIVERY_ID)
            .orderId(ORDER_ID)
            .customerAddress(address)
            .restaurantAddress(address)
            .deliveryExecutive(unavailableDeliveryExecutive)
            .status(DeliveryStatus.ASSIGNED)
            .build();

    public static final DeliveryUpdateResponse expectedDeliveryUpdateResponseToPickedUp = DeliveryUpdateResponse.builder()
            .orderId(ORDER_ID)
            .deliveryExecutiveId(availableDeliveryExecutive.getId())
            .status(DeliveryStatus.PICKED_UP)
            .build();

    public static final DeliveryUpdateResponse expectedDeliveryUpdateResponseToDelivered = DeliveryUpdateResponse.builder()
            .orderId(ORDER_ID)
            .deliveryExecutiveId(availableDeliveryExecutive.getId())
            .status(DeliveryStatus.DELIVERED)
            .build();
}
