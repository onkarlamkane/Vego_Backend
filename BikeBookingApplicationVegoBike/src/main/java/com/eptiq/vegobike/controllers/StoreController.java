//package com.eptiq.vegobike.controllers;
//
//import com.eptiq.vegobike.dtos.*;
//import com.eptiq.vegobike.services.StoreService;
//import com.eptiq.vegobike.utils.ImageUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.validation.Valid;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/stores")
//public class StoreController {
//
//    private final StoreService storeService;
//    private final ImageUtils imageUtils;
//
//    public StoreController(StoreService storeService, ImageUtils imageUtils) {
//        this.storeService = storeService;
//        this.imageUtils = imageUtils;
//    }
//
//    // Create Store
//    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Map<String, Object>> addStore(
//            @Valid @ModelAttribute StoreCreateRequest dto,
//            HttpServletRequest request) {
//
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        log.info("Store creation request - name: {}, hasImage: {}",
//                dto.getStoreName(), (dto.getStoreImage() != null && !dto.getStoreImage().isEmpty()));
//
//        try {
//            StoreResponse created = storeService.create(dto, dto.getStoreImage());
//            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
//                    "success", true,
//                    "message", "Store created successfully",
//                    "data", enrich(created),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception e) {
//            log.error("Store creation failed - error: {}", e.getMessage(), e);
//            return errorResponse("STORE_CREATE_FAILED", e.getMessage(), correlationId);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//    // Update Store
//    @PostMapping("/edit/{id}")
//    public ResponseEntity<Map<String, Object>> updateStore(
//            @PathVariable Integer id,
//            @ModelAttribute StoreUpdateRequest dto,
//            @RequestParam(name = "storeImage", required = false) MultipartFile storeImage,
//            HttpServletRequest request) {
//
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        log.info("Store update request - ID: {}, hasImage: {}", id, (storeImage != null && !storeImage.isEmpty()));
//
//        try {
//            StoreResponse updated = storeService.update(id, dto, storeImage);
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "Store updated successfully",
//                    "data", enrich(updated),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception e) {
//            log.error("Store update failed - ID: {}, error: {}", id, e.getMessage(), e);
//            return errorResponse("STORE_UPDATE_FAILED", e.getMessage(), correlationId);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//    // List All Stores (paginated)
//    @GetMapping("/all")
//    public ResponseEntity<Map<String, Object>> listAllStores(
//            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
//            HttpServletRequest request) {
//
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        try {
//            Page<StoreResponse> stores = storeService.getAll(pageable).map(this::enrich);
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "data", stores.getContent(),
//                    "pagination", Map.of(
//                            "currentPage", stores.getNumber(),
//                            "totalPages", stores.getTotalPages(),
//                            "totalElements", stores.getTotalElements(),
//                            "hasNext", stores.hasNext(),
//                            "hasPrevious", stores.hasPrevious()
//                    ),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception e) {
//            return errorResponse("STORE_LIST_FAILED", "Failed to fetch stores", correlationId);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//    // Get Store by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getStoreById(@PathVariable Integer id, HttpServletRequest request) {
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        try {
//            StoreResponse store = storeService.get(id);
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "data", enrich(store),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception e) {
//            return errorResponse("STORE_NOT_FOUND", e.getMessage(), correlationId, HttpStatus.NOT_FOUND);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//
//
//
//    @GetMapping("/active")
//    public ResponseEntity<Map<String, Object>> getAllActiveStores(HttpServletRequest request) {
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        log.info("GET_ALL_ACTIVE_STORES_REQUEST - correlationId: {}", correlationId);
//
//        try {
//            List<StoreResponse> activeStores = storeService.getAllActiveStores();
//            List<StoreResponse> enrichedStores = activeStores.stream()
//                    .map(this::enrich)
//                    .toList();
//
//            log.info("GET_ALL_ACTIVE_STORES_SUCCESS - found {} active stores", enrichedStores.size());
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "Active stores retrieved successfully",
//                    "data", enrichedStores,
//                    "count", enrichedStores.size(),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception e) {
//            log.error("GET_ALL_ACTIVE_STORES_FAILED - error: {}", e.getMessage(), e);
//            return errorResponse("ACTIVE_STORES_FETCH_FAILED", e.getMessage(), correlationId);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//
//    @PutMapping("/{id}/status")
//    public ResponseEntity<Map<String, Object>> toggleStoreStatus(
//            @PathVariable Integer id,
//            HttpServletRequest request) {
//
//        String correlationId = generateCorrelationId();
//        MDC.put("correlationId", correlationId);
//        MDC.put("clientIP", getClientIP(request));
//
//        log.info("TOGGLE_STORE_STATUS_REQUEST - storeId: {}, action: toggle active/inactive status", id);
//
//        try {
//            StoreResponse toggledStore = storeService.toggleStatus(id);
//
//            String statusMessage = toggledStore.getIsActive() == 1 ?
//                    "Store activated successfully" : "Store deactivated successfully";
//
//            String statusDescription = toggledStore.getIsActive() == 1 ?
//                    "Store is now ACTIVE and available" : "Store is now INACTIVE and unavailable";
//
//            log.info("TOGGLE_STORE_STATUS_SUCCESS - storeId: {}, newStatus: {}, action: {}",
//                    id, toggledStore.getIsActive(),
//                    toggledStore.getIsActive() == 1 ? "ACTIVATED" : "DEACTIVATED");
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", statusMessage,
//                    "description", statusDescription,
//                    "data", enrich(toggledStore),
//                    "statusChange", Map.of(
//                            "previousStatus", toggledStore.getIsActive() == 1 ? 0 : 1,
//                            "currentStatus", toggledStore.getIsActive(),
//                            "previousStatusText", toggledStore.getIsActive() == 1 ? "INACTIVE" : "ACTIVE",
//                            "currentStatusText", toggledStore.getIsActive() == 1 ? "ACTIVE" : "INACTIVE"
//                    ),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//
//        } catch (Exception e) {
//            log.error("TOGGLE_STORE_STATUS_FAILED - storeId: {}, error: {}", id, e.getMessage(), e);
//            return errorResponse("STORE_STATUS_TOGGLE_FAILED", e.getMessage(), correlationId);
//        } finally {
//            MDC.clear();
//        }
//    }
//
//
//    // ===== Utility =====
//    private StoreResponse enrich(StoreResponse dto) {
//        if (dto != null && dto.getStoreImage() != null) {
//            dto.setStoreImage(imageUtils.getPublicUrl(dto.getStoreImage()));
//        }
//        return dto;
//    }
//
//    private ResponseEntity<Map<String, Object>> errorResponse(String code, String message, String correlationId) {
//        return errorResponse(code, message, correlationId, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    private ResponseEntity<Map<String, Object>> errorResponse(String code, String message, String correlationId, HttpStatus status) {
//        return ResponseEntity.status(status).body(Map.of(
//                "success", false,
//                "error", code,
//                "message", message,
//                "timestamp", LocalDateTime.now(),
//                "correlationId", correlationId
//        ));
//    }
//
//    private String generateCorrelationId() {
//        return UUID.randomUUID().toString().substring(0, 8);
//    }
//
//    private String getClientIP(HttpServletRequest request) {
//        String xf = request.getHeader("X-Forwarded-For");
//        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
//        String xr = request.getHeader("X-Real-IP");
//        if (xr != null && !xr.isBlank()) return xr;
//        return request.getRemoteAddr();
//    }
//}



package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.services.StoreService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final ImageUtils imageUtils;

    public StoreController(StoreService storeService, ImageUtils imageUtils) {
        this.storeService = storeService;
        this.imageUtils = imageUtils;
    }

    // Create Store
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addStore(
            @Valid @ModelAttribute StoreCreateRequest dto,
            HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        log.info("Store creation request - name: {}, hasImage: {}",
                dto.getStoreName(), (dto.getStoreImage() != null && !dto.getStoreImage().isEmpty()));

        try {
            StoreResponse created = storeService.create(dto, dto.getStoreImage());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Store created successfully",
                    "data", enrich(created),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("Store creation failed - error: {}", e.getMessage(), e);
            return errorResponse("STORE_CREATE_FAILED", e.getMessage(), correlationId);
        } finally {
            MDC.clear();
        }
    }

    // Update Store
    @PostMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> updateStore(
            @PathVariable Integer id,
            @ModelAttribute StoreUpdateRequest dto,
            @RequestParam(name = "storeImage", required = false) MultipartFile storeImage,
            HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        log.info("Store update request - ID: {}, hasImage: {}", id, (storeImage != null && !storeImage.isEmpty()));

        try {
            StoreResponse updated = storeService.update(id, dto, storeImage);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Store updated successfully",
                    "data", enrich(updated),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("Store update failed - ID: {}, error: {}", id, e.getMessage(), e);
            return errorResponse("STORE_UPDATE_FAILED", e.getMessage(), correlationId);
        } finally {
            MDC.clear();
        }
    }

    // List All Stores (paginated) - ENHANCED
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> listAllStores(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        log.info("LIST_ALL_STORES - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<StoreResponse> stores = storeService.getAll(pageable);

            // Apply enrichment to each store
            Page<StoreResponse> enrichedStores = stores.map(store -> {
                StoreResponse enriched = enrich(store);
                log.debug("Enriched store - ID: {}, Original image: {}, Enriched image: {}",
                        store.getId(), store.getStoreImage(), enriched.getStoreImage());
                return enriched;
            });

            log.info("LIST_ALL_STORES_SUCCESS - returned {} stores", enrichedStores.getNumberOfElements());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enrichedStores.getContent(),
                    "pagination", Map.of(
                            "currentPage", enrichedStores.getNumber(),
                            "totalPages", enrichedStores.getTotalPages(),
                            "totalElements", enrichedStores.getTotalElements(),
                            "hasNext", enrichedStores.hasNext(),
                            "hasPrevious", enrichedStores.hasPrevious()
                    ),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("LIST_ALL_STORES_FAILED - error: {}", e.getMessage(), e);
            return errorResponse("STORE_LIST_FAILED", "Failed to fetch stores", correlationId);
        } finally {
            MDC.clear();
        }
    }

    // Get Store by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStoreById(@PathVariable Integer id, HttpServletRequest request) {
        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        try {
            StoreResponse store = storeService.get(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enrich(store),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return errorResponse("STORE_NOT_FOUND", e.getMessage(), correlationId, HttpStatus.NOT_FOUND);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActiveStores(HttpServletRequest request) {
        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        log.info("GET_ALL_ACTIVE_STORES_REQUEST - correlationId: {}", correlationId);

        try {
            List<StoreResponse> activeStores = storeService.getAllActiveStores();
            List<StoreResponse> enrichedStores = activeStores.stream()
                    .map(this::enrich)
                    .toList();

            log.info("GET_ALL_ACTIVE_STORES_SUCCESS - found {} active stores", enrichedStores.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Active stores retrieved successfully",
                    "data", enrichedStores,
                    "count", enrichedStores.size(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("GET_ALL_ACTIVE_STORES_FAILED - error: {}", e.getMessage(), e);
            return errorResponse("ACTIVE_STORES_FETCH_FAILED", e.getMessage(), correlationId);
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleStoreStatus(
            @PathVariable Integer id,
            HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", getClientIP(request));

        log.info("TOGGLE_STORE_STATUS_REQUEST - storeId: {}, action: toggle active/inactive status", id);

        try {
            StoreResponse toggledStore = storeService.toggleStatus(id);

            String statusMessage = toggledStore.getIsActive() == 1 ?
                    "Store activated successfully" : "Store deactivated successfully";

            String statusDescription = toggledStore.getIsActive() == 1 ?
                    "Store is now ACTIVE and available" : "Store is now INACTIVE and unavailable";

            log.info("TOGGLE_STORE_STATUS_SUCCESS - storeId: {}, newStatus: {}, action: {}",
                    id, toggledStore.getIsActive(),
                    toggledStore.getIsActive() == 1 ? "ACTIVATED" : "DEACTIVATED");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", statusMessage,
                    "description", statusDescription,
                    "data", enrich(toggledStore),
                    "statusChange", Map.of(
                            "previousStatus", toggledStore.getIsActive() == 1 ? 0 : 1,
                            "currentStatus", toggledStore.getIsActive(),
                            "previousStatusText", toggledStore.getIsActive() == 1 ? "INACTIVE" : "ACTIVE",
                            "currentStatusText", toggledStore.getIsActive() == 1 ? "ACTIVE" : "INACTIVE"
                    ),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));

        } catch (Exception e) {
            log.error("TOGGLE_STORE_STATUS_FAILED - storeId: {}, error: {}", id, e.getMessage(), e);
            return errorResponse("STORE_STATUS_TOGGLE_FAILED", e.getMessage(), correlationId);
        } finally {
            MDC.clear();
        }
    }

    // ===== ENHANCED UTILITY METHODS =====

    /**
     * Enhanced enrich method with proper logging and null safety
     */
    private StoreResponse enrich(StoreResponse dto) {
        if (dto == null) {
            log.warn("Cannot enrich null StoreResponse");
            return null;
        }

        if (dto.getStoreImage() != null && !dto.getStoreImage().trim().isEmpty()) {
            String originalPath = dto.getStoreImage();
            String enrichedUrl = imageUtils.getPublicUrl(originalPath);
            dto.setStoreImage(enrichedUrl);

            log.debug("Image enrichment - Store ID: {}, Original: {}, Enriched: {}",
                    dto.getId(), originalPath, enrichedUrl);
        } else {
            log.debug("No image to enrich for store ID: {}", dto.getId());
        }

        return dto;
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String code, String message, String correlationId) {
        return errorResponse(code, message, correlationId, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String code, String message, String correlationId, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "error", code,
                "message", message,
                "timestamp", LocalDateTime.now(),
                "correlationId", correlationId
        ));
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String getClientIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        String xr = request.getHeader("X-Real-IP");
        if (xr != null && !xr.isBlank()) return xr;
        return request.getRemoteAddr();
    }
}