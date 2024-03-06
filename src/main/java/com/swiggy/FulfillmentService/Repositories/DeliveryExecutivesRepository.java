package com.swiggy.FulfillmentService.Repositories;

import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Enums.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryExecutivesRepository extends JpaRepository<DeliveryExecutive, Long> {
    Optional<DeliveryExecutive> findByUsername(String username);
    boolean existsByUsername(String username);
    List<DeliveryExecutive> findByAvailability(Availability availability);
}
