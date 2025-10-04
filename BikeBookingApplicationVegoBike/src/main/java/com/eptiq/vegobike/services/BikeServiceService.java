package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.BikeServiceDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BikeServiceService {

    // Basic CRUD
    BikeServiceDto createBikeService(BikeServiceDto dto);

    BikeServiceDto createBikeServiceWithImage(BikeServiceDto dto, MultipartFile imageFile) throws IOException;

    BikeServiceDto updateBikeService(Long id, BikeServiceDto dto);

    BikeServiceDto updateBikeServiceWithImage(Long id, BikeServiceDto dto, MultipartFile imageFile) throws IOException;

    BikeServiceDto getBikeServiceById(Long id);

    Page<BikeServiceDto> getAllBikeServices(int page, int size);

    void deleteBikeService(Long id);

    // Filtering methods
    List<BikeServiceDto> getBikeServicesByStatus(String status);

    List<BikeServiceDto> getBikeServicesByType(String serviceType);

    List<BikeServiceDto> getBikeServicesByBrandAndModel(Integer brandId, Integer modelId);

    List<BikeServiceDto> getServicesByBrandModelAndType(Integer brandId, Integer modelId, String serviceType);


}
