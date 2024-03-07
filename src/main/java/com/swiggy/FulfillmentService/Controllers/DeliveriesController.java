package com.swiggy.FulfillmentService.Controllers;

import com.swiggy.FulfillmentService.DTOs.ApiErrorResponse;
import com.swiggy.FulfillmentService.DTOs.DeliveryRequest;
import com.swiggy.FulfillmentService.DTOs.DeliveryResponse;
import com.swiggy.FulfillmentService.DTOs.DeliveryUpdateResponse;
import com.swiggy.FulfillmentService.Exceptions.*;
import com.swiggy.FulfillmentService.Services.DeliveriesService;
import com.swiggy.FulfillmentService.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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

    @PutMapping("/{deliveryId}")
    public ResponseEntity<?> updateStatus(@PathVariable(value = "deliveryId") String id) {
        try {
            String username = SecurityUtils.getCurrentUsername();
            DeliveryUpdateResponse deliveryUpdateResponse = deliveriesService.updateStatus(id, username);
            return ResponseEntity.status(HttpStatus.OK).body(deliveryUpdateResponse);
        } catch (InvalidAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (NoSuchElementException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (UnauthorizedStatusUpdateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (OrderAlreadyDeliveredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
