package com.swiggy.FulfillmentService.Controllers;

import com.swiggy.FulfillmentService.DTOs.ApiErrorResponse;
import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationRequest;
import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationResponse;
import com.swiggy.FulfillmentService.Services.DeliveryExecutivesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveryExecutives")
public class DeliveryExecutivesController {

    private final DeliveryExecutivesService deliveryExecutivesService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid DeliveryExecutiveRegistrationRequest deliveryExecutiveRegistrationRequest) {
        try {
            DeliveryExecutiveRegistrationResponse deliveryExecutiveRegistrationResponse = deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(deliveryExecutiveRegistrationResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
