package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.ServiceOrderDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ServiceOrderService {
    ServiceOrderDTO createOrder(ServiceOrderDTO dto);

    ServiceOrderDTO getOrderById(Long id);

    List<ServiceOrderDTO> getAllOrders();

    // 🆕 New method for fetching service order history by user ID
    List<ServiceOrderDTO> getOrdersByCustomer(HttpServletRequest request);
    ServiceOrderDTO updateOrder(Long id, ServiceOrderDTO dto); // ✅ New update method

}
