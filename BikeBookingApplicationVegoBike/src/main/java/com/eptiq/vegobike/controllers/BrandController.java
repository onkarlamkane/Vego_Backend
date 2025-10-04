
package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BrandDTO;
import com.eptiq.vegobike.services.BrandService;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService service;
    private final ImageUtils imageUtils;

    @Autowired
    public BrandController(BrandService service, ImageUtils imageUtils) {
        this.service = service;
        this.imageUtils = imageUtils;
    }

    // ✅ Utility to add public URL to BrandDTO response
    private BrandDTO enrichWithImageUrl(BrandDTO dto) {
        if (dto != null && dto.getBrandImage() != null) {
            dto.setBrandImage(imageUtils.getPublicUrl(dto.getBrandImage()));
        }
        return dto;
    }

    // ✅ Utility for list enrichment
    private List<BrandDTO> enrichList(List<BrandDTO> list) {
        return list.stream().map(this::enrichWithImageUrl).collect(Collectors.toList());
    }

    // Create Brand
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addBrand(
            @RequestPart("brandName") String brandName,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        try {
            BrandDTO dto = new BrandDTO();
            dto.setBrandName(brandName.trim());
            dto.setIsActive(1);

            BrandDTO created = service.create(dto, image);
            created = enrichWithImageUrl(created);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Brand created successfully",
                    "data", created,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "BRAND_CREATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    // Update Brand
    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editBrand(
            @PathVariable Integer id,
            @RequestPart(name = "brandName", required = false) String brandName,
            @RequestPart(name = "image", required = false) MultipartFile image) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        try {
            BrandDTO dto = new BrandDTO();
            dto.setBrandName(brandName);
            BrandDTO updated = service.update(id, dto, image);
            updated = enrichWithImageUrl(updated);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Brand updated successfully",
                    "data", updated,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("BRAND_UPDATE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "BRAND_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    // Toggle Status
    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> toggleStatus(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            service.toggleStatus(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Status updated",
                    "id", id,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "STATUS_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    // Delete Brand
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrand(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Brand deleted successfully",
                    "id", id,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "BRAND_DELETE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            BrandDTO dto = service.getById(id);
            dto = enrichWithImageUrl(dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", dto,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "BRAND_NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    // List paged
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> listAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BrandDTO> page = service.list(pageable);
        List<BrandDTO> enriched = enrichList(page.getContent());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", enriched,
                "pagination", Map.of(
                        "currentPage", page.getNumber(),
                        "totalPages", page.getTotalPages(),
                        "totalElements", page.getTotalElements(),
                        "hasNext", page.hasNext(),
                        "hasPrevious", page.hasPrevious()
                ),
                "timestamp", LocalDateTime.now()
        ));
    }

    // List active
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> active() {
        List<BrandDTO> list = service.listActive();
        list = enrichList(list);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "count", list.size(),
                "timestamp", LocalDateTime.now()
        ));
    }

}
