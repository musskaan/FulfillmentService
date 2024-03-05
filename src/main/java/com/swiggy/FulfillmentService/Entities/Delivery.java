package com.swiggy.FulfillmentService.Entities;

import com.swiggy.FulfillmentService.DTOs.Location;
import com.swiggy.FulfillmentService.Enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Location restaurantAddress;

    @ManyToOne
    @JoinColumn(name = "deliveryExecutive_id")
    private DeliveryExecutive deliveryExecutive;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;
}
