package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.ServiceOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderItemRepository extends JpaRepository<ServiceOrderItem, Long> {

    List<ServiceOrderItem> findByOrderId(int orderId);

    List<ServiceOrderItem> findByCustomerId(int customerId);
}

