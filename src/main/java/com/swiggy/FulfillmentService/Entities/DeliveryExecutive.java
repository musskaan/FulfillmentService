package com.swiggy.FulfillmentService.Entities;

import com.swiggy.FulfillmentService.DTOs.Location;
import com.swiggy.FulfillmentService.Enums.Availability;
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
@Table(name = "delivery_executives")
public class DeliveryExecutive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Availability availability = Availability.AVAILABLE;
}
