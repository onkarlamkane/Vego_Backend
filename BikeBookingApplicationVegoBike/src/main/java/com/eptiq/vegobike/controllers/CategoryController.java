package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.CategoryDTO;
import com.eptiq.vegobike.services.CategoryService;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ImageUtils imageUtils;

    @Autowired
    public CategoryController(CategoryService categoryService, ImageUtils imageUtils) {
        this.categoryService = categoryService;
        this.imageUtils = imageUtils;
    }

    // Utility to add public image URL (null-safe + logging)
    private CategoryDTO enrichWithImageUrl(CategoryDTO dto) {
        if (dto == null) return null;
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            String original = dto.getImage();
            String enriched = imageUtils.getPublicUrl(original);
            dto.setImage(enriched);
            log.debug("CATEGORY_ENRICH - id: {}, original: {}, enriched: {}", dto.getId(), original, enriched);
        }
        return dto;
    }

    private List<CategoryDTO> enrichList(List<CategoryDTO> list) {
        return list.stream().map(this::enrichWithImageUrl).collect(Collectors.toList());
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addCategory(
            @RequestParam("categoryName") String categoryName,
            @RequestParam("vehicleTypeId") Integer vehicleTypeId,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        try {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryName(categoryName.trim());
            dto.setVehicleTypeId(vehicleTypeId);
            dto.setIsActive(1);

            CategoryDTO created = categoryService.createCategory(dto, image);
            created = enrichWithImageUrl(created);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Category created successfully",
                    "data", created,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("CATEGORY_CREATE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "CATEGORY_CREATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editCategory(
            @PathVariable Integer id,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("vehicleTypeId") Integer vehicleTypeId,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        try {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryName(categoryName);
            dto.setVehicleTypeId(vehicleTypeId);

            CategoryDTO updated = categoryService.updateCategory(id, dto, image);
            updated = enrichWithImageUrl(updated);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Category updated successfully",
                    "data", updated,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("CATEGORY_UPDATE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", "CATEGORY_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> toggleStatus(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            categoryService.toggleCategoryStatus(id);
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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            CategoryDTO dto = categoryService.getCategoryById(id);
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
                    "error", "CATEGORY_NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> listAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CategoryDTO> page = categoryService.getAllCategories(pageable);
        List<CategoryDTO> enriched = enrichList(page.getContent());

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

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> active() {
        List<CategoryDTO> list = categoryService.getActiveCategories();
        list = enrichList(list);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "count", list.size(),
                "timestamp", LocalDateTime.now()
        ));
    }
}