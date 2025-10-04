package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.VehicleTypeDto;
import com.eptiq.vegobike.services.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {
    private final VehicleTypeService service;

    @GetMapping("/all")
    public ResponseEntity<List<VehicleTypeDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<VehicleTypeDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<VehicleTypeDto> create(@RequestBody VehicleTypeDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VehicleTypeDto> update(@PathVariable Integer id, @RequestBody VehicleTypeDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<VehicleTypeDto> toggleStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }


}
