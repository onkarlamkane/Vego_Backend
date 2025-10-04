//package com.eptiq.vegobike.services.impl;
//
//import com.eptiq.vegobike.dtos.*;
//import com.eptiq.vegobike.mappers.StoreMapper;
//import com.eptiq.vegobike.model.Store;
//import com.eptiq.vegobike.repositories.StoreRepository;
//import com.eptiq.vegobike.services.StoreService;
//import com.eptiq.vegobike.utils.ImageUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@Slf4j
//public class StoreServiceImpl implements StoreService {
//
//    private final StoreRepository repository;
//    private final StoreMapper mapper;
//    private final ImageUtils imageUtils;
//
//    public StoreServiceImpl(StoreRepository repository, StoreMapper mapper, ImageUtils imageUtils) {
//        this.repository = repository;
//        this.mapper = mapper;
//        this.imageUtils = imageUtils;
//    }
//
//    @Override
//    public StoreResponse create(StoreCreateRequest request, MultipartFile image) {
//        Store entity = mapper.toEntity(request);
//        if (image != null && !image.isEmpty()) {
//            try {
//                String storedPath = imageUtils.storeStoreImage(image);
//                entity.setStoreImage(storedPath);
//            } catch (IOException e) {
//                throw new IllegalStateException("Failed to store image", e);
//            }
//        }
//        Store saved = repository.save(entity);
//        return mapper.toResponse(saved);
//    }
//
//    @Override
//    public StoreResponse update(Integer id, StoreUpdateRequest request, MultipartFile image) {
//        Store entity = repository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
//
//        mapper.updateEntityFromDto(request, entity);
//
//        if (image != null && !image.isEmpty()) {
//            try {
//                String storedPath = imageUtils.storeStoreImage(image);
//                entity.setStoreImage(storedPath);
//            } catch (IOException e) {
//                throw new IllegalStateException("Failed to update image", e);
//            }
//        }
//        Store saved = repository.save(entity);
//        return mapper.toResponse(saved);
//    }
//
//
//    @Override
//    public StoreResponse get(Integer id) {
//        return repository.findById(id)
//                .map(mapper::toResponse)
//                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
//    }
//
//    @Override
//    public Page<StoreResponse> getAll(Pageable pageable) {
//        return repository.findAll(pageable).map(mapper::toResponse);
//    }
//
//    @Override
//    public Page<StoreResponse> searchByName(String name, Pageable pageable) {
//        if (name == null || name.isBlank()) {
//            return getAll(pageable);
//        }
//        return repository.findByStoreNameContainingIgnoreCase(name.trim(), pageable)
//                .map(mapper::toResponse);
//    }
//
//    @Override
//    public Page<StoreResponse> findNearbyStores(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
//        return repository.findStoresWithinRadius(latitude, longitude, radiusKm, pageable)
//                .map(mapper::toResponse);
//    }
//
////    @Override
////    public StoreResponse toggleStatus(Integer id) {
////        Store entity = repository.findByIdIgnoringStatus(id);
////        if (entity == null) {
////            throw new IllegalArgumentException("Store not found");
////        }
////        entity.setIsActive((entity.getIsActive() != null && entity.getIsActive() == 1) ? 0 : 1);
////        return mapper.toResponse(repository.save(entity));
////    }
//
//    @Override
//    public Long getActiveStoreCount() {
//        return repository.countByIsActive(1);
//    }
//
//
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<StoreResponse> getAllActiveStores() {
//        log.info("STORE_SERVICE_GET_ALL_ACTIVE - Fetching all active stores");
//
//        try {
//            List<Store> activeStores = repository.findByIsActive(1);
//
//            log.info("STORE_SERVICE_GET_ALL_ACTIVE_SUCCESS - Found {} active stores",
//                    activeStores.size());
//
//            if (log.isDebugEnabled()) {
//                activeStores.forEach(store ->
//                        log.debug("Active Store - ID: {}, Name: {}, Status: {}",
//                                store.getId(), store.getStoreName(), store.getIsActive()));
//            }
//
//            return activeStores.stream()
//                    .map(this::mapToDto)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("STORE_SERVICE_GET_ALL_ACTIVE_FAILED - Error fetching active stores: {}",
//                    e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch active stores", e);
//        }
//    }
//
//    @Override
//    public StoreResponse toggleStatus(Integer id) throws Exception {
//        log.info("STORE_SERVICE_TOGGLE_STATUS - Toggling status for store ID: {}", id);
//
//        try {
//            // Use the repository method that bypasses @Where clause for soft-deleted records
//            Store store = repository.findByIdIgnoringStatus(id);
//
//            if (store == null) {
//                log.warn("STORE_SERVICE_TOGGLE_STATUS_FAILED - Store not found with ID: {}", id);
//                throw new RuntimeException("Store not found with ID " + id);
//            }
//
//            Integer oldStatus = store.getIsActive();
//            String oldStatusText = oldStatus == 1 ? "ACTIVE" : "INACTIVE";
//
//            log.debug("STORE_SERVICE_TOGGLE_STATUS - Store found: ID={}, Name={}, Current status={} ({})",
//                    store.getId(), store.getStoreName(), oldStatus, oldStatusText);
//
//            // Toggle status using the model method
//            store.toggleStatus();
//            Store savedStore = repository.save(store);
//
//            String newStatusText = savedStore.getIsActive() == 1 ? "ACTIVE" : "INACTIVE";
//
//            log.info("STORE_SERVICE_TOGGLE_STATUS_SUCCESS - Store status toggled: ID={}, Name={}, {} → {}",
//                    savedStore.getId(), savedStore.getStoreName(), oldStatusText, newStatusText);
//
//            return mapToDto(savedStore);
//
//        } catch (Exception e) {
//            log.error("STORE_SERVICE_TOGGLE_STATUS_FAILED - Error toggling status for store ID: {}, Error: {}",
//                    id, e.getMessage(), e);
//            throw new Exception("Failed to toggle store status: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Map Store entity to StoreResponse DTO
//     */
//    private StoreResponse mapToDto(Store store) {
//        log.debug("STORE_SERVICE_MAP_TO_DTO - Mapping store entity to DTO: ID={}", store.getId());
//
//        StoreResponse dto = new StoreResponse();
//        BeanUtils.copyProperties(store, dto);
//
//        // Don't enrich image URL here - let the controller handle it
//        return dto;
//    }
//}





package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.mappers.StoreMapper;
import com.eptiq.vegobike.model.Store;
import com.eptiq.vegobike.repositories.StoreRepository;
import com.eptiq.vegobike.services.StoreService;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository repository;
    private final StoreMapper mapper;
    private final ImageUtils imageUtils;

    public StoreServiceImpl(StoreRepository repository, StoreMapper mapper, ImageUtils imageUtils) {
        this.repository = repository;
        this.mapper = mapper;
        this.imageUtils = imageUtils;
    }

    @Override
    public StoreResponse create(StoreCreateRequest request, MultipartFile image) {
        log.info("Creating store: {}", request.getStoreName());

        Store entity = mapper.toEntity(request);
        if (image != null && !image.isEmpty()) {
            try {
                String storedPath = imageUtils.storeStoreImage(image);
                entity.setStoreImage(storedPath);
                log.info("Store image stored at: {}", storedPath);
            } catch (IOException e) {
                log.error("Failed to store image for store: {}", request.getStoreName(), e);
                throw new IllegalStateException("Failed to store image", e);
            }
        }
        Store saved = repository.save(entity);
        log.info("Store created successfully with ID: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public StoreResponse update(Integer id, StoreUpdateRequest request, MultipartFile image) {
        log.info("Updating store ID: {}", id);

        Store entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        mapper.updateEntityFromDto(request, entity);

        if (image != null && !image.isEmpty()) {
            try {
                // Delete old image if exists
                if (entity.getStoreImage() != null) {
                    imageUtils.deleteImage(entity.getStoreImage());
                }

                String storedPath = imageUtils.storeStoreImage(image);
                entity.setStoreImage(storedPath);
                log.info("Store image updated at: {}", storedPath);
            } catch (IOException e) {
                log.error("Failed to update image for store ID: {}", id, e);
                throw new IllegalStateException("Failed to update image", e);
            }
        }
        Store saved = repository.save(entity);
        log.info("Store updated successfully: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public StoreResponse get(Integer id) {
        log.debug("Fetching store by ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    @Override
    public Page<StoreResponse> getAll(Pageable pageable) {
        log.info("Fetching all stores - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<StoreResponse> stores = repository.findAll(pageable).map(store -> {
            StoreResponse response = mapper.toResponse(store);
            log.debug("Store fetched - ID: {}, Name: {}, Image: {}",
                    response.getId(), response.getStoreName(), response.getStoreImage());
            return response;
        });

        log.info("Fetched {} stores out of {} total", stores.getNumberOfElements(), stores.getTotalElements());
        return stores;
    }

    @Override
    public Page<StoreResponse> searchByName(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return getAll(pageable);
        }
        log.info("Searching stores by name: {}", name);
        return repository.findByStoreNameContainingIgnoreCase(name.trim(), pageable)
                .map(mapper::toResponse);
    }

    @Override
    public Page<StoreResponse> findNearbyStores(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        log.info("Finding nearby stores - lat: {}, lng: {}, radius: {}km", latitude, longitude, radiusKm);
        return repository.findStoresWithinRadius(latitude, longitude, radiusKm, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public Long getActiveStoreCount() {
        Long count = repository.countByIsActive(1);
        log.debug("Active store count: {}", count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponse> getAllActiveStores() {
        log.info("STORE_SERVICE_GET_ALL_ACTIVE - Fetching all active stores");

        try {
            List<Store> activeStores = repository.findByIsActive(1);

            log.info("STORE_SERVICE_GET_ALL_ACTIVE_SUCCESS - Found {} active stores",
                    activeStores.size());

            if (log.isDebugEnabled()) {
                activeStores.forEach(store ->
                        log.debug("Active Store - ID: {}, Name: {}, Status: {}, Image: {}",
                                store.getId(), store.getStoreName(), store.getIsActive(), store.getStoreImage()));
            }

            return activeStores.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("STORE_SERVICE_GET_ALL_ACTIVE_FAILED - Error fetching active stores: {}",
                    e.getMessage(), e);
            throw new RuntimeException("Failed to fetch active stores", e);
        }
    }

    @Override
    public StoreResponse toggleStatus(Integer id) throws Exception {
        log.info("STORE_SERVICE_TOGGLE_STATUS - Toggling status for store ID: {}", id);

        try {
            Store store = repository.findByIdIgnoringStatus(id);

            if (store == null) {
                log.warn("STORE_SERVICE_TOGGLE_STATUS_FAILED - Store not found with ID: {}", id);
                throw new RuntimeException("Store not found with ID " + id);
            }

            Integer oldStatus = store.getIsActive();
            String oldStatusText = oldStatus == 1 ? "ACTIVE" : "INACTIVE";

            log.debug("STORE_SERVICE_TOGGLE_STATUS - Store found: ID={}, Name={}, Current status={} ({})",
                    store.getId(), store.getStoreName(), oldStatus, oldStatusText);

            store.toggleStatus();
            Store savedStore = repository.save(store);

            String newStatusText = savedStore.getIsActive() == 1 ? "ACTIVE" : "INACTIVE";

            log.info("STORE_SERVICE_TOGGLE_STATUS_SUCCESS - Store status toggled: ID={}, Name={}, {} → {}",
                    savedStore.getId(), savedStore.getStoreName(), oldStatusText, newStatusText);

            return mapToDto(savedStore);

        } catch (Exception e) {
            log.error("STORE_SERVICE_TOGGLE_STATUS_FAILED - Error toggling status for store ID: {}, Error: {}",
                    id, e.getMessage(), e);
            throw new Exception("Failed to toggle store status: " + e.getMessage(), e);
        }
    }

    /**
     * Map Store entity to StoreResponse DTO
     */
    private StoreResponse mapToDto(Store store) {
        log.debug("STORE_SERVICE_MAP_TO_DTO - Mapping store entity to DTO: ID={}, Image={}",
                store.getId(), store.getStoreImage());

        StoreResponse dto = new StoreResponse();
        BeanUtils.copyProperties(store, dto);

        // Don't enrich image URL here - let the controller handle it
        return dto;
    }
}