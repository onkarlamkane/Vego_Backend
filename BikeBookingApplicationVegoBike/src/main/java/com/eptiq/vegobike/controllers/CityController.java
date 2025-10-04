package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.CityDto;
import com.eptiq.vegobike.services.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping
    public ResponseEntity<List<CityDto>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Integer id) {
        return ResponseEntity.ok(cityService.getCityById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<CityDto> createCity(
            @RequestPart("cityDto") CityDto cityDto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(cityService.createCity(cityDto, image));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CityDto> updateCity(
            @PathVariable Integer id,
            @RequestPart("cityDto") CityDto cityDto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(cityService.updateCity(id, cityDto, image));
    }


    @GetMapping("/active")
    public ResponseEntity<List<CityDto>> getActiveCities() {
        return ResponseEntity.ok(cityService.getActiveCities());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<CityDto> toggleCityStatus(@PathVariable Integer id) {
        CityDto updatedCity = cityService.toggleCityStatus(id);
        return ResponseEntity.ok(updatedCity);
    }




}