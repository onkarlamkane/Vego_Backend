package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByCustomerId(int customerId);

    List<ServiceOrder> findByStoreId(int storeId);

    List<ServiceOrder> findByOrderStatus(int orderStatus);

    ServiceOrder findByOrderId(String orderId);

    List<ServiceOrder> findByCustomerId(Long customerId);



}
