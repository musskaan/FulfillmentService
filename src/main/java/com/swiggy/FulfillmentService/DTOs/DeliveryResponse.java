package com.swiggy.FulfillmentService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponse {
    private Long orderId;
    private Address restaurantAddress;
    private Address customerAddress;
    private DeliveryExecutiveDTO deliveryExecutive;
    private String message;
}
