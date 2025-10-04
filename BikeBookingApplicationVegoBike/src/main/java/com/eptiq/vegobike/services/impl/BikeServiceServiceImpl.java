
package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BikeServiceDto;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.BikeServiceMapper;
import com.eptiq.vegobike.model.BikeService;
import com.eptiq.vegobike.repositories.BikeServiceRepository;
import com.eptiq.vegobike.services.BikeServiceService;
import com.eptiq.vegobike.utils.ImageUtils;
import com.eptiq.vegobike.enums.ServiceType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BikeServiceServiceImpl implements BikeServiceService {

    private final BikeServiceRepository repository;
    private final ImageUtils imageUtils;
    private final BikeServiceMapper mapper;

    public BikeServiceServiceImpl(BikeServiceRepository repository,
                                  ImageUtils imageUtils,
                                  BikeServiceMapper mapper) {
        this.repository = repository;
        this.imageUtils = imageUtils;
        this.mapper = mapper;
    }

    @Override
    public BikeServiceDto createBikeService(BikeServiceDto dto) {
        log.info("BIKE_SERVICE_CREATE - Creating new bike service: {}", dto.getServiceName());
        BikeService entity = mapper.toEntity(dto);
        entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        BikeService saved = repository.save(entity);
        BikeServiceDto result = mapper.toDto(saved);

        if (result.getServiceImage() != null && imageUtils != null) {
            result.setServiceImage(imageUtils.getPublicUrl(result.getServiceImage()));
        }

        return result;
    }

    @Override
    public BikeServiceDto createBikeServiceWithImage(BikeServiceDto dto, MultipartFile imageFile) throws IOException {
        log.info("BIKE_SERVICE_CREATE_WITH_IMAGE - Creating bike service with image: {}", dto.getServiceName());

        if (isValidImageFile(imageFile) && imageUtils != null) {
            String imagePath = imageUtils.storeBikeServiceImage(imageFile);
            dto.setServiceImage(imagePath);
        }

        BikeService entity = mapper.toEntity(dto);
        entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        BikeService saved = repository.save(entity);
        BikeServiceDto result = mapper.toDto(saved);

        if (result.getServiceImage() != null && imageUtils != null) {
            result.setServiceImage(imageUtils.getPublicUrl(result.getServiceImage()));
        }

        return result;
    }

    @Override
    public BikeServiceDto updateBikeService(Long id, BikeServiceDto dto) {
        log.info("BIKE_SERVICE_UPDATE - Updating bike service with ID: {}", id);
        BikeService entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike Service not found with id: " + id));

        mapper.updateEntityFromDto(dto, entity);
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        BikeService updated = repository.save(entity);
        BikeServiceDto result = mapper.toDto(updated);

        if (result.getServiceImage() != null && imageUtils != null) {
            result.setServiceImage(imageUtils.getPublicUrl(result.getServiceImage()));
        }

        return result;
    }

    @Override
    public BikeServiceDto updateBikeServiceWithImage(Long id, BikeServiceDto dto, MultipartFile imageFile) throws IOException {
        log.info("BIKE_SERVICE_UPDATE_WITH_IMAGE - Updating bike service with ID: {}", id);
        BikeService entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike Service not found with id: " + id));

        String oldImagePath = entity.getServiceImage();

        if (isValidImageFile(imageFile) && imageUtils != null) {
            if (oldImagePath != null && !oldImagePath.trim().isEmpty()) {
                deleteImageSafely(oldImagePath);
            }
            String newImagePath = imageUtils.storeBikeServiceImage(imageFile);
            dto.setServiceImage(newImagePath);
        }

        mapper.updateEntityFromDto(dto, entity);
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        BikeService updated = repository.save(entity);
        BikeServiceDto result = mapper.toDto(updated);

        if (result.getServiceImage() != null && imageUtils != null) {
            result.setServiceImage(imageUtils.getPublicUrl(result.getServiceImage()));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BikeServiceDto getBikeServiceById(Long id) {
        BikeService entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike Service not found with id: " + id));
        BikeServiceDto result = mapper.toDto(entity);

        if (result.getServiceImage() != null && imageUtils != null) {
            result.setServiceImage(imageUtils.getPublicUrl(result.getServiceImage()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BikeServiceDto> getAllBikeServices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BikeService> bikeServicesPage = repository.findAll(pageable);

        List<BikeServiceDto> dtoList = bikeServicesPage.getContent()
                .stream()
                .map(mapper::toDto)
                .peek(dto -> {
                    if (dto.getServiceImage() != null && imageUtils != null) {
                        dto.setServiceImage(imageUtils.getPublicUrl(dto.getServiceImage()));
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, bikeServicesPage.getTotalElements());
    }

    @Override
    public void deleteBikeService(Long id) {
        BikeService entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeService not found with id: " + id));

        String imagePath = entity.getServiceImage();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            deleteImageSafely(imagePath);
        }

        repository.delete(entity);
    }
//    @Override
//    public List<ModelResponse> getModelsByBrand(Integer brandId) {
//        List<Model> models = modelRepository.findByBrandIdAndIsActiveTrue(brandId);
//        return models.stream()
//                .map(modelMapper::toResponse)
//                .collect(Collectors.toList());
//    }


    @Override
    @Transactional(readOnly = true)
    public List<BikeServiceDto> getBikeServicesByBrandAndModel(Integer brandId, Integer modelId) {

        List<BikeService> services = repository.findByBrandIdAndModelId(brandId, modelId);

        return services.stream()
                .map(mapper::toDto)
                .peek(dto -> {
                    if (dto.getServiceImage() != null && imageUtils != null) {
                        dto.setServiceImage(imageUtils.getPublicUrl(dto.getServiceImage()));
                    }
                })
                .collect(Collectors.toList());
    }




    @Transactional(readOnly = true)
    public List<BikeServiceDto> getBikeServicesByStatus(String status) {
        Integer statusCode = switch (status.toUpperCase()) {
            case "ACTIVE" -> 1;
            case "INACTIVE" -> 0;
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };

        return repository.findByStatusCode(statusCode)
                .stream()
                .map(mapper::toDto)
                .peek(dto -> {
                    if (dto.getServiceImage() != null && imageUtils != null) {
                        dto.setServiceImage(imageUtils.getPublicUrl(dto.getServiceImage()));
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeServiceDto> getBikeServicesByType(String serviceType) {
        ServiceType typeEnum;
        try {
            typeEnum = ServiceType.valueOf(serviceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + serviceType, e);
        }

        return repository.findByServiceTypeCode(typeEnum.getCode())
                .stream()
                .map(mapper::toDto)
                .peek(dto -> {
                    if (dto.getServiceImage() != null && imageUtils != null) {
                        dto.setServiceImage(imageUtils.getPublicUrl(dto.getServiceImage()));
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BikeServiceDto> getServicesByBrandModelAndType(Integer brandId, Integer modelId, String serviceType) {

        // Default to GENERAL_SERVICE
        ServiceType typeEnum = ServiceType.GENERAL_SERVICE;
        if (serviceType != null && !serviceType.isBlank()) {
            try {
                typeEnum = ServiceType.valueOf(serviceType.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                typeEnum = ServiceType.GENERAL_SERVICE;
            }
        }

        Integer typeCode = typeEnum.getCode();

        // Fetch filtered services directly from DB
        List<BikeService> services = repository.findByBrandIdAndModelIdAndServiceTypeCode(brandId, modelId, typeCode);

        return services.stream()
                .map(mapper::toDto)
                .peek(dto -> {
                    try {
                        if (dto.getServiceImage() != null && imageUtils != null) {
                            dto.setServiceImage(imageUtils.getPublicUrl(dto.getServiceImage()));
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get image URL for service {}: {}", dto.getServiceName(), e.getMessage());
                        dto.setServiceImage(null);
                    }
                })
                .collect(Collectors.toList());
    }


    // --- Helper Methods ---
    private boolean isValidImageFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private void deleteImageSafely(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty() && imageUtils != null) {
            try {
                imageUtils.deleteImage(imagePath);
            } catch (Exception e) {
                log.error("Failed to delete image {}: {}", imagePath, e.getMessage(), e);
            }
        }
    }
}
