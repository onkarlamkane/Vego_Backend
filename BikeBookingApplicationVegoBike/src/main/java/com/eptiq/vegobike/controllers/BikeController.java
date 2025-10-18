package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.AvailableBikeDto;
import com.eptiq.vegobike.dtos.BikeDocumentsDTO;
import com.eptiq.vegobike.dtos.BikeRequestDTO;
import com.eptiq.vegobike.dtos.BikeResponseDTO;
import com.eptiq.vegobike.services.BikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService bikeService;

    @PostMapping("/add")
    public ResponseEntity<BikeResponseDTO> createBike(@ModelAttribute BikeRequestDTO request) {
        try {
            return ResponseEntity.ok(bikeService.createBike(request));
        } catch (IOException | MultipartException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BikeResponseDTO> updateBike(
            @PathVariable int id,
            @ModelAttribute BikeRequestDTO request
    ) {
        try {
            return ResponseEntity.ok(bikeService.updateBike(id, request));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BikeResponseDTO>> getAllBikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Sorting by createdAt descending
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // Or "updatedAt"
        return ResponseEntity.ok(bikeService.getAllBikes(pageable));
    }



    @GetMapping("/{id}")
    public ResponseEntity<BikeResponseDTO> getBikeById(@PathVariable int id) {
        return ResponseEntity.ok(bikeService.getBikeById(id));
    }



    @GetMapping("/available")
    public ResponseEntity<Page<AvailableBikeDto>> getAvailableBikes(
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,

            @RequestParam("endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,

            @RequestParam(value = "addressType", required = false) String addressType,

            @RequestParam(value = "search", required = false) String search,

            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<AvailableBikeDto> bikes = bikeService.getAvailableBikes(
                startDate, endDate, addressType, search, pageable);

        return ResponseEntity.ok(bikes);
    }

    @GetMapping("/view-documents/{id}")
    public ResponseEntity<BikeDocumentsDTO> viewBikeDocuments(@PathVariable int id) {
        BikeDocumentsDTO documents = bikeService.getBikeDocuments(id);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BikeResponseDTO>> searchBikes(@RequestParam(required = false) String query) {
        List<BikeResponseDTO> bikes = bikeService.searchBikes(query);
        return ResponseEntity.ok(bikes);
    }





}
