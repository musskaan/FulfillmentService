package com.swiggy.FulfillmentService.Builders;

import com.swiggy.FulfillmentService.DTOs.*;
import com.swiggy.FulfillmentService.Entities.Delivery;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.swiggy.FulfillmentService.Constants.Constants.SUCCESSFULLY_ASSIGNED;
import static com.swiggy.FulfillmentService.Constants.Constants.SUCCESSFULLY_REGISTERED;

public class Builder {

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

    public static DeliveryResponse buildDeliveryResponse(DeliveryExecutive deliveryExecutive, DeliveryRequest deliveryRequest) {
        return DeliveryResponse.builder()
                .orderId(deliveryRequest.getOrderId())
                .restaurantLocation(deliveryRequest.getPickupLocation())
                .customerLocation(deliveryRequest.getDropLocation())
                .deliveryExecutive(
                        new DeliveryExecutiveDTO(deliveryExecutive.getFirstName(),
                        deliveryExecutive.getLastName(),
                        deliveryExecutive.getPhone()))
                .message(SUCCESSFULLY_ASSIGNED)
                .build();
    }

    public static Delivery buildDelivery(DeliveryExecutive closestAvailableDeliveryExecutive, DeliveryRequest deliveryRequest) {
        return Delivery.builder()
                .orderId(deliveryRequest.getOrderId())
                .restaurantLocation(deliveryRequest.getPickupLocation())
                .customerLocation(deliveryRequest.getDropLocation())
                .deliveryExecutive(closestAvailableDeliveryExecutive)
                .build();
    }
}
