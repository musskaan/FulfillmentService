package com.swiggy.FulfillmentService.Repositories;

import com.swiggy.FulfillmentService.Entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveriesRepository extends JpaRepository<Delivery, String> {
    boolean existsByOrderId(Long orderId);
}
