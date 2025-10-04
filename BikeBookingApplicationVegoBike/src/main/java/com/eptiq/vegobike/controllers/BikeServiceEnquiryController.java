package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BikeServiceEnquiryDto;
import com.eptiq.vegobike.services.BikeServiceEnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bike-service-cart")
@RequiredArgsConstructor
public class BikeServiceEnquiryController {

    private final BikeServiceEnquiryService service;

    @PostMapping("/add")
    public ResponseEntity<BikeServiceEnquiryDto> addToCart(@RequestBody BikeServiceEnquiryDto dto) {
        return ResponseEntity.ok(service.addToCart(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BikeServiceEnquiryDto>> getAllServices() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BikeServiceEnquiryDto>> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getCartByCustomer(customerId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        service.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }
}
