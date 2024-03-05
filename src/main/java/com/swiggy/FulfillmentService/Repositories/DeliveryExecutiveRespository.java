package com.swiggy.FulfillmentService.Repositories;

import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryExecutiveRespository extends JpaRepository<DeliveryExecutive, Long> {
    Optional<DeliveryExecutive> findByUsername(String username);
}
