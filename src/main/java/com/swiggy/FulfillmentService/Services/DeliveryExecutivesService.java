package com.swiggy.FulfillmentService.Services;

import com.swiggy.FulfillmentService.Builders.Builder;
import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationRequest;
import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationResponse;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Repositories.DeliveryExecutivesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryExecutivesService {

    private final DeliveryExecutivesRepository deliveryExecutivesRepository;

    private final PasswordEncoder passwordEncoder;

    public DeliveryExecutiveRegistrationResponse register(DeliveryExecutiveRegistrationRequest registrationRequest) {
        try {
            if (deliveryExecutivesRepository.existsByUsername(registrationRequest.getUsername())) {
                throw new IllegalArgumentException("User with the given username already exists");
            }

            DeliveryExecutive deliveryExecutive = Builder.buildDeliveryExecutive(passwordEncoder, registrationRequest);
            deliveryExecutivesRepository.save(deliveryExecutive);
            return Builder.buildDeliveryExecutiveRegistrationResponse(registrationRequest);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while saving delivery executive to the database", e);
        }
    }
}
