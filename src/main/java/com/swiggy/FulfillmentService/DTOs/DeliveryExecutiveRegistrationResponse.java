package com.swiggy.FulfillmentService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryExecutiveRegistrationResponse {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private Location location;
    private String message;
}
