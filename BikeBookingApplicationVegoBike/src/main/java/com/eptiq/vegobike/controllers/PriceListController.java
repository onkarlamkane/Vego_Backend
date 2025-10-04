package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.PriceListDTO;
import com.eptiq.vegobike.services.PriceListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Validated
public class PriceListController {

    private final PriceListService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPriceLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("PRICE_LIST_CONTROLLER - Getting all price lists: page={}, size={}", page, size);

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PriceListDTO> prices = service.getAllPriceLists(pageable);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", prices.getContent(),
                    "pagination", Map.of(
                            "currentPage", prices.getNumber(),
                            "totalPages", prices.getTotalPages(),
                            "totalElements", prices.getTotalElements(),
                            "hasNext", prices.hasNext(),
                            "hasPrevious", prices.hasPrevious()
                    ),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get all price lists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActivePriceLists() {
        log.info("PRICE_LIST_CONTROLLER - Getting active price lists");

        try {
            List<PriceListDTO> prices = service.getActivePriceLists();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", prices,
                    "count", prices.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get active price lists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPriceListById(@PathVariable @Positive Long id) {
        log.info("PRICE_LIST_CONTROLLER - Getting price list with ID: {}", id);

        try {
            PriceListDTO priceList = service.getPriceListById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", priceList,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get price list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createPriceList(@Valid @RequestBody PriceListDTO dto) {
        log.info("PRICE_LIST_CONTROLLER - Creating price list: Category={}, Days={}",
                dto.getCategoryId(), dto.getDays());

        try {
            PriceListDTO created = service.createPriceList(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Price list created successfully",
                    "data", created,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to create price list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "CREATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePriceList(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PriceListDTO dto) {

        log.info("PRICE_LIST_CONTROLLER - Updating price list with ID: {}", id);

        try {
            PriceListDTO updated = service.updatePriceList(id, dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Price list updated successfully",
                    "data", updated,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to update price list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePriceList(@PathVariable @Positive Long id) {
        log.info("PRICE_LIST_CONTROLLER - Deleting price list with ID: {}", id);

        try {
            service.deletePriceList(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Price list deleted successfully",
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to delete price list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "DELETE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getPricesByCategory(@PathVariable @Positive Integer categoryId) {
        log.info("PRICE_LIST_CONTROLLER - Getting prices for category: {}", categoryId);

        try {
            List<PriceListDTO> prices = service.getPriceListsByCategory(categoryId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", prices,
                    "count", prices.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get prices by category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/category/{categoryId}/days/{days}")
    public ResponseEntity<Map<String, Object>> getPriceByCategoryAndDays(
            @PathVariable @Positive Integer categoryId,
            @PathVariable Integer days) {

        log.info("PRICE_LIST_CONTROLLER - Getting price: Category={}, Days={}", categoryId, days);

        try {
            PriceListDTO price = service.getPriceByCategoryAndDays(categoryId, days);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", price,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get price by category and days: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/hourly")
    public ResponseEntity<Map<String, Object>> getHourlyRates() {
        log.info("PRICE_LIST_CONTROLLER - Getting hourly rates");

        try {
            List<PriceListDTO> rates = service.getHourlyRates();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", rates,
                    "count", rates.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get hourly rates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyRates() {
        log.info("PRICE_LIST_CONTROLLER - Getting daily rates");

        try {
            List<PriceListDTO> rates = service.getDailyRates();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", rates,
                    "count", rates.size(),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get daily rates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePriceListStatus(
            @PathVariable @Positive Long id,
            @RequestParam boolean isActive) {

        log.info("PRICE_LIST_CONTROLLER - Updating status for ID: {} to: {}", id, isActive);

        try {
            PriceListDTO updated = service.updatePriceListStatus(id, isActive);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Price list status updated successfully",
                    "data", updated,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to update status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "STATUS_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/affordable")
    public ResponseEntity<Map<String, Object>> getAffordablePrices(
            @RequestParam @Positive BigDecimal maxPrice) {

        log.info("PRICE_LIST_CONTROLLER - Getting affordable prices under: {}", maxPrice);

        try {
            List<PriceListDTO> prices = service.getAffordablePrices(maxPrice);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", prices,
                    "count", prices.size(),
                    "maxPrice", maxPrice,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("PRICE_LIST_CONTROLLER - Failed to get affordable prices: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }
}
