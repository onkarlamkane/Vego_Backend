package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BikeServiceDto;
import com.eptiq.vegobike.services.impl.BikeServiceServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bike-services")
@RequiredArgsConstructor
public class BikeServiceController {

    private final BikeServiceServiceImpl service;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createBikeService(@RequestBody BikeServiceDto dto) {
        log.info("BIKE_SERVICE_CONTROLLER - Creating bike service: {}", dto.getServiceName());

        try {
            BikeServiceDto created = service.createBikeService(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Bike service created successfully",
                    "data", created,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to create bike service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "CREATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }



    @PostMapping("/create-with-image")
    public ResponseEntity<Map<String, Object>> createBikeServiceWithImage(
            @RequestPart("bikeService") String bikeServiceJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        BikeServiceDto dto = objectMapper.readValue(bikeServiceJson, BikeServiceDto.class);

        BikeServiceDto created = service.createBikeServiceWithImage(dto, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Bike service created successfully with image",
                "data", created,
                "timestamp", LocalDateTime.now()
        ));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBikeServiceById(@PathVariable Long id) {
        log.info("BIKE_SERVICE_CONTROLLER - Fetching bike service with ID: {}", id);

        try {
            BikeServiceDto bikeService = service.getBikeServiceById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", bikeService,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to get bike service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBikeServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("BIKE_SERVICE_CONTROLLER - Fetching all bike services (page: {}, size: {})", page, size);

        try {
            Page<BikeServiceDto> services = service.getAllBikeServices(page, size);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services.getContent(),
                    "pagination", Map.of(
                            "currentPage", services.getNumber(),
                            "totalPages", services.getTotalPages(),
                            "totalElements", services.getTotalElements(),
                            "hasNext", services.hasNext(),
                            "hasPrevious", services.hasPrevious()
                    ),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to get all bike services: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBikeService(
            @PathVariable Long id,
            @RequestBody BikeServiceDto dto) {

        log.info("BIKE_SERVICE_CONTROLLER - Updating bike service with ID: {}", id);

        try {
            BikeServiceDto updated = service.updateBikeService(id, dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bike service updated successfully",
                    "data", updated,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to update bike service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }


@PutMapping("/{id}/with-image")
public ResponseEntity<Map<String, Object>> updateBikeServiceWithImage(
        @PathVariable Long id,
        @RequestPart("bikeService") String bikeServiceJson,
        @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

    ObjectMapper objectMapper = new ObjectMapper();
    BikeServiceDto dto = objectMapper.readValue(bikeServiceJson, BikeServiceDto.class);

    BikeServiceDto updated = service.updateBikeServiceWithImage(id, dto, imageFile);

    return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Bike service updated successfully with image",
            "data", updated,
            "timestamp", LocalDateTime.now()
    ));
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBikeService(@PathVariable Long id) {
        log.info("BIKE_SERVICE_CONTROLLER - Deleting bike service with ID: {}", id);

        try {
            service.deleteBikeService(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bike service deleted successfully",
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to delete bike service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "DELETE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<Map<String, Object>> getBikeServicesByStatus(@PathVariable String status) {
        log.info("BIKE_SERVICE_CONTROLLER - Fetching bike services by status: {}", status);

        try {
            List<BikeServiceDto> services = service.getBikeServicesByStatus(status);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services,
                    "count", services.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to get services by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_BY_STATUS_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/by-type/{serviceType}")
    public ResponseEntity<Map<String, Object>> getBikeServicesByType(@PathVariable String serviceType) {
        log.info("BIKE_SERVICE_CONTROLLER - Fetching bike services by type: {}", serviceType);

        try {
            List<BikeServiceDto> services = service.getBikeServicesByType(serviceType);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services,
                    "count", services.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("BIKE_SERVICE_CONTROLLER - Failed to get services by type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_BY_TYPE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/by-brand-model")
    public ResponseEntity<Map<String, Object>> getBikeServicesByBrandAndModel(
            @RequestParam Integer brandId,
            @RequestParam Integer modelId)  {

        log.info("Fetching bike services for Brand ID={} and Model ID={}", brandId, modelId);

        try {
            List<BikeServiceDto> services = service.getBikeServicesByBrandAndModel( brandId, modelId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services,
                    "count", services.size(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Failed to fetch services for brandId={} and modelId={}: {}", brandId, modelId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_BY_BRAND_MODEL_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/by-brand-model-type")
    public ResponseEntity<Map<String, Object>> getServicesByBrandModelAndType(
            @RequestParam Integer brandId,
            @RequestParam Integer modelId,
            @RequestParam(required = false) String serviceType) {

        try {
            List<BikeServiceDto> services = service.getServicesByBrandModelAndType(brandId, modelId, serviceType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services,
                    "count", services.size(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error fetching services: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }


}
