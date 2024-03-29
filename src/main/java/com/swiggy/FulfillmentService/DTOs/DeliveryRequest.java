package com.swiggy.FulfillmentService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequest {
    private Long orderId;
    private Address pickupAddress;
    private Address dropAddress;
}
