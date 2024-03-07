package com.swiggy.FulfillmentService.DTOs;

import com.swiggy.FulfillmentService.Enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryUpdateResponse {
    private String id;
    private Long orderId;
    private String deliveryExecutiveId;
    private DeliveryStatus status;
}
