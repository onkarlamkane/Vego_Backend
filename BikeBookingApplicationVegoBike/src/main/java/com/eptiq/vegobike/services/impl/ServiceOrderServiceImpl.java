package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.ServiceOrderDTO;
import com.eptiq.vegobike.enums.ServiceAddressType;
import com.eptiq.vegobike.mappers.ServiceOrderMapper;
import com.eptiq.vegobike.model.ServiceOrder;
import com.eptiq.vegobike.repositories.ServiceOrderRepository;
import com.eptiq.vegobike.services.JwtService;
import com.eptiq.vegobike.services.ServiceOrderService;
import com.razorpay.Customer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderMapper serviceOrderMapper;
    private final JwtService jwtService;


    // ✅ Create a new service order
    @Override
    public ServiceOrderDTO createOrder(ServiceOrderDTO dto) {
        if (dto.getServiceAddressType() == ServiceAddressType.DOORSTEP) {
            if (!StringUtils.hasText(dto.getDoorstepAddress())) {
                throw new IllegalArgumentException("Doorstep address is required for DOORSTEP service.");
            }
            dto.setStoreId(0); // No store for doorstep
        }

        ServiceOrder order = serviceOrderMapper.toEntity(dto);
        order = serviceOrderRepository.save(order);
        return serviceOrderMapper.toDto(order);
    }

    // ✅ Get order by ID
    @Override
    public ServiceOrderDTO getOrderById(Long id) {
        return serviceOrderRepository.findById(id)
                .map(serviceOrderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ✅ Get all orders
    @Override
    public List<ServiceOrderDTO> getAllOrders() {
        return serviceOrderRepository.findAll()
                .stream()
                .map(serviceOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // 🆕 ✅ Get all orders for a specific customer
    @Override
    public List<ServiceOrderDTO> getOrdersByCustomer(HttpServletRequest request) {
        // 1️⃣ Extract customer ID from JWT in request
        Long customerId = extractCustomerIdFromToken(request);

        if (customerId == null) {
            throw new RuntimeException("Unable to extract customer ID from token");
        }

        // 2️⃣ Fetch orders by customer ID
        List<ServiceOrder> orders = serviceOrderRepository.findByCustomerId(customerId);

        // 3️⃣ Map to DTO
        return orders.stream()
                .map(serviceOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // Helper method to extract customer ID from token
    private Long extractCustomerIdFromToken(HttpServletRequest request) {
        try {
            Long customerId = jwtService.extractCustomerIdFromRequest(request);
            log.debug("🔐 CUSTOMER_ID_EXTRACTED - CustomerID: {}", customerId);
            return customerId;
        } catch (Exception e) {
            log.error("🚫 CUSTOMER_ID_EXTRACTION_FAILED - Error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    // ✅ Update Order by ID
    @Override
    public ServiceOrderDTO updateOrder(Long id, ServiceOrderDTO dto) {
        ServiceOrder existingOrder = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service order not found"));

        // Update fields
        existingOrder.setServiceAddressType(dto.getServiceAddressType());
        existingOrder.setDoorstepAddress(dto.getDoorstepAddress());
        existingOrder.setVehicleNumber(dto.getVehicleNumber());
        existingOrder.setChasisNumber(dto.getChasisNumber());
        existingOrder.setEngineNumber(dto.getEngineNumber());
        existingOrder.setOrderAmount(dto.getOrderAmount());
        existingOrder.setFinalAmount(dto.getFinalAmount());
        existingOrder.setPaymentMethod(dto.getPaymentMethod());
        existingOrder.setPaymentStatus(dto.getPaymentStatus());
        existingOrder.setSlotTime(dto.getSlotTime());
        existingOrder.setServiceComments(dto.getServiceComments());
        existingOrder.setNextServiceDate(dto.getNextServiceDate());
        existingOrder.setOrderStatus(dto.getOrderStatus());

        // Save and return
        serviceOrderRepository.save(existingOrder);
        return serviceOrderMapper.toDto(existingOrder);
    }

}
