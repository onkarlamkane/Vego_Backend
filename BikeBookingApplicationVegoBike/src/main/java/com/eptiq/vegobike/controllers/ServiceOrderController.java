package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.ServiceOrderDTO;
import com.eptiq.vegobike.services.ServiceOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    public ResponseEntity<ServiceOrderDTO> createOrder(@RequestBody ServiceOrderDTO dto) {
        return ResponseEntity.ok(serviceOrderService.createOrder(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderDTO>> getAllOrders() {
        return ResponseEntity.ok(serviceOrderService.getAllOrders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderDTO> updateOrder(@PathVariable Long id, @RequestBody ServiceOrderDTO dto) {
        ServiceOrderDTO updatedOrder = serviceOrderService.updateOrder(id, dto);
        return ResponseEntity.ok(updatedOrder);
    }

    // 🆕 ✅ Get all orders for a specific user (Service Order History)

    @GetMapping("/customer")
    public ResponseEntity<List<ServiceOrderDTO>> getOrdersByCustomer(HttpServletRequest request) {
        List<ServiceOrderDTO> orders = serviceOrderService.getOrdersByCustomer(request);
        return ResponseEntity.ok(orders);
    }
}
