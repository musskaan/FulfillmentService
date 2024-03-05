package com.swiggy.FulfillmentService.Builders;

import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationRequest;
import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationResponse;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Builder {

    private static final String SUCCESSFULLY_REGISTERED = "Delivery executive registered successfully";

    public static DeliveryExecutiveRegistrationResponse buildDeliveryExecutiveRegistrationResponse(DeliveryExecutiveRegistrationRequest registrationRequest) {
        return DeliveryExecutiveRegistrationResponse.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .username(registrationRequest.getUsername())
                .phone(registrationRequest.getPhone())
                .location(registrationRequest.getLocation())
                .message(SUCCESSFULLY_REGISTERED)
                .build();
    }

    public static DeliveryExecutive buildDeliveryExecutive(PasswordEncoder passwordEncoder, DeliveryExecutiveRegistrationRequest registrationRequest) {
        return DeliveryExecutive.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phone(registrationRequest.getPhone())
                .location(registrationRequest.getLocation())
                .build();
    }
}
