package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoreService {
    StoreResponse create(StoreCreateRequest request, MultipartFile image);
    StoreResponse update(Integer id, StoreUpdateRequest request, MultipartFile image);
    StoreResponse get(Integer id);
    Page<StoreResponse> getAll(Pageable pageable);
    Page<StoreResponse> searchByName(String name, Pageable pageable);
    Page<StoreResponse> findNearbyStores(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    // StoreResponse toggleStatus(Integer id);
    Long getActiveStoreCount();
    // List<StoreResponse> listActive();

    /**
     * Get all active stores (isActive = 1)
     * @return List of active store responses
     */
    List<StoreResponse> getAllActiveStores();

    /**
     * Toggle store status (active/inactive)
     * @param id Store ID
     * @return Updated store response with new status
     * @throws Exception if toggle fails
     */
    StoreResponse toggleStatus(Integer id) throws Exception;
}