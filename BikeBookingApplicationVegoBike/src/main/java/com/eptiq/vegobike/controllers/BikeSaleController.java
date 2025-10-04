package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.services.BikeSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bike-sales")
public class BikeSaleController {

    private static final Logger logger = LoggerFactory.getLogger(BikeSaleController.class);
    private final BikeSaleService bikeSaleService;

    public BikeSaleController(BikeSaleService bikeSaleService) {
        this.bikeSaleService = bikeSaleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBikeSale(@PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to get bike sale with ID: {} (CorrelationId: {})", id, correlationId);

        try {
            BikeSaleDTO bikeSaleDTO = bikeSaleService.getBikeSaleById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", bikeSaleDTO,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while fetching bike sale with ID: {} (CorrelationId: {})", id, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_FETCH_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBikeSale(@PathVariable Long id, @RequestBody BikeSaleDTO bikeSaleDTO) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to update bike sale with ID: {} (CorrelationId: {})", id, correlationId);

        try {
            BikeSaleDTO updatedBikeSaleDTO = bikeSaleService.updateBikeSale(id, bikeSaleDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedBikeSaleDTO,
                    "message", "Bike sale updated successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while updating bike sale with ID: {} (CorrelationId: {})", id, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_UPDATE_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PostMapping("/{bikeId}/images")
    public ResponseEntity<Map<String, Object>> updateBikeImages(@PathVariable Long bikeId, @RequestBody BikeSellImageDTO bikeImageDTO) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to update bike images for bike ID: {} (CorrelationId: {})", bikeId, correlationId);

        try {
            BikeSellImageDTO updatedBikeImageDTO = bikeSaleService.updateBikeImages(bikeId, bikeImageDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedBikeImageDTO,
                    "message", "Bike images updated successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while updating images for bike ID: {} (CorrelationId: {})", bikeId, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_IMAGES_UPDATE_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBikeSale(@RequestBody BikeSaleDTO bikeSaleDTO) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to add bike sale (CorrelationId: {})", correlationId);

        try {
            BikeSaleDTO savedBikeSale = bikeSaleService.saveBikeSale(bikeSaleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "data", savedBikeSale,
                    "message", "Bike sale added successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while adding bike sale (CorrelationId: {})", correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_ADD_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }
}

