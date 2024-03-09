package com.swiggy.FulfillmentService.Entities;

import com.swiggy.FulfillmentService.DTOs.Address;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Long orderId;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "restaurant_street")),
            @AttributeOverride(name = "city", column = @Column(name = "restaurant_city")),
            @AttributeOverride(name = "state", column = @Column(name = "restaurant_state")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "restaurant_zipcode")),
    })
    @Column(nullable = false)
    private Address restaurantAddress;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "customer_street")),
            @AttributeOverride(name = "city", column = @Column(name = "customer_city")),
            @AttributeOverride(name = "state", column = @Column(name = "customer_state")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "customer_zipcode")),
    })
    @Column(nullable = false)
    private Address customerAddress;

    @ManyToOne
    @JoinColumn(name = "deliveryExecutive_id")
    private DeliveryExecutive deliveryExecutive;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;
}
