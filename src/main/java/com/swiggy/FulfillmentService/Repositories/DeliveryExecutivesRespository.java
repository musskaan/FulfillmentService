package com.swiggy.FulfillmentService.Repositories;

import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryExecutivesRespository extends JpaRepository<DeliveryExecutive, Long> {
    Optional<DeliveryExecutive> findByUsername(String username);
    boolean existsByUsername(String username);
}
