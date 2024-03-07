package com.swiggy.FulfillmentService.Controllers;

import com.swiggy.FulfillmentService.DTOs.ApiErrorResponse;
import com.swiggy.FulfillmentService.DTOs.DeliveryRequest;
import com.swiggy.FulfillmentService.DTOs.DeliveryResponse;
import com.swiggy.FulfillmentService.Exceptions.NoDeliveryExecutiveNearbyException;
import com.swiggy.FulfillmentService.Exceptions.OrderAlreadyAssignedException;
import com.swiggy.FulfillmentService.Services.DeliveriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveriesController {

    private final DeliveriesService deliveriesService;

    @PostMapping
    public ResponseEntity<?> assign(@RequestBody DeliveryRequest request) {
        try {
            DeliveryResponse deliveryResponse = deliveriesService.assign(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(deliveryResponse);
        } catch (OrderAlreadyAssignedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value()));
        } catch (NoDeliveryExecutiveNearbyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
