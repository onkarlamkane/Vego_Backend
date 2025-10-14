package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.ModelCreateRequest;
import com.eptiq.vegobike.dtos.ModelResponse;
import com.eptiq.vegobike.dtos.ModelUpdateRequest;
import com.eptiq.vegobike.services.ModelService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService service;
    private final ImageUtils imageUtils;

    public ModelController(ModelService service, ImageUtils imageUtils) {
        this.service = service;
        this.imageUtils = imageUtils;
    }

    private ModelResponse enrichWithImageUrl(ModelResponse dto) {
        if (dto != null && dto.getModelImage() != null) {
            dto.setModelImage(imageUtils.getPublicUrl(dto.getModelImage()));
        }
        return dto;
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addModel(
            @RequestParam(name = "brandId") Integer brandId,
            @RequestParam(name = "modelName") String modelName,
            @RequestPart(name = "modelImage", required = false) MultipartFile modelImage) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            // ✅ Build DTO manually
            ModelCreateRequest dto = new ModelCreateRequest();
            dto.setBrandId(brandId);
            dto.setModelName(modelName);

            ModelResponse created = service.create(dto, modelImage);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Model created successfully",
                    "data", enrichWithImageUrl(created),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("MODEL_CREATE_FAILED - CID: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "MODEL_CREATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editModel(
            @PathVariable Integer id,
            @Valid @RequestPart("request") ModelUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            ModelResponse updated = service.update(id, request, image);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Model updated successfully",
                    "data", enrichWithImageUrl(updated),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("MODEL_UPDATE_FAILED - CID: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "MODEL_UPDATE_FAILED",
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
            ModelResponse dto = service.toggleStatus(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Status updated",
                    "data", enrichWithImageUrl(dto),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "MODEL_STATUS_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteModel(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Model deleted successfully",
                    "id", id,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "MODEL_DELETE_FAILED",
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
            ModelResponse dto = service.getById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enrichWithImageUrl(dto),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "MODEL_NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> listAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ModelResponse> page = service.list(pageable);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", page.getContent().stream().map(this::enrichWithImageUrl).collect(Collectors.toList()),
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

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ModelResponse> page = service.searchByName(query, pageable);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", page.getContent().stream().map(this::enrichWithImageUrl).collect(Collectors.toList()),
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
        List<ModelResponse> list = service.listActive()
                .stream()
                .map(this::enrichWithImageUrl)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "count", list.size(),
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/by-brand/{id}")
    public ResponseEntity<Map<String, Object>> getModelsByBrand(@PathVariable Integer id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            List<ModelResponse> models = service.getModelsByBrand(id)
                    .stream()
                    .map(this::enrichWithImageUrl)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", models,
                    "count", models.size(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            log.error("MODEL_FETCH_BY_BRAND_FAILED - CID: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "MODEL_FETCH_BY_BRAND_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

}
