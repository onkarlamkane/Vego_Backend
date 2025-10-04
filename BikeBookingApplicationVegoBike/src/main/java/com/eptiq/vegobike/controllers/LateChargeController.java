package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.LateChargeRequestDTO;
import com.eptiq.vegobike.dtos.LateChargeResponseDTO;
import com.eptiq.vegobike.services.LateChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/late-charges")
@RequiredArgsConstructor
public class LateChargeController {

    private final LateChargeService service;

    @PostMapping("/add")
    public ResponseEntity<LateChargeResponseDTO> create(@RequestBody LateChargeRequestDTO request) {
        return ResponseEntity.ok(service.createLateCharge(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LateChargeResponseDTO> update(@PathVariable Integer id,
                                                        @RequestBody LateChargeRequestDTO request) {
        return ResponseEntity.ok(service.updateLateCharge(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LateChargeResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getLateChargeById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LateChargeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllLateCharges());
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteLateCharge(id);
        return ResponseEntity.noContent().build();
    }


    // Get only active records
    @GetMapping("/active")
    public ResponseEntity<List<LateChargeResponseDTO>> getAllActive() {
        return ResponseEntity.ok(service.getAllActiveLateCharges());
    }

    // Change status
    @PutMapping("/{id}/status")
    public ResponseEntity<LateChargeResponseDTO> changeStatus(
            @PathVariable Integer id,
            @RequestParam("isActive") Integer isActive) {
        return ResponseEntity.ok(service.changeStatus(id, isActive));
    }

}
