package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.mappers.BikeSaleMapper;
import com.eptiq.vegobike.mappers.BikeSellImageMapper;
import com.eptiq.vegobike.model.BikeSale;
import com.eptiq.vegobike.model.BikeSellImage;
import com.eptiq.vegobike.repositories.BikeSaleRepository;
import com.eptiq.vegobike.repositories.BikeSellImageRepository;
import com.eptiq.vegobike.services.BikeSaleService;
import com.eptiq.vegobike.utils.ImageUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class BikeSaleServiceImpl implements BikeSaleService {

    private final BikeSaleRepository bikeSaleRepository;
    private final BikeSellImageRepository bikeSellImageRepository;
    private final BikeSaleMapper bikeSaleMapper;
    private final BikeSellImageMapper bikeSellImageMapper;
    private final ImageUtils imageUtils;  // ✅ NEW: Added ImageUtils dependency

    public BikeSaleServiceImpl(BikeSaleRepository bikeSaleRepository,
                               BikeSellImageRepository bikeSellImageRepository,
                               BikeSaleMapper bikeSaleMapper,
                               BikeSellImageMapper bikeSellImageMapper,
                               ImageUtils imageUtils) {
        this.bikeSaleRepository = bikeSaleRepository;
        this.bikeSellImageRepository = bikeSellImageRepository;
        this.bikeSaleMapper = bikeSaleMapper;
        this.bikeSellImageMapper = bikeSellImageMapper;
        this.imageUtils = imageUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public BikeSaleDTO getBikeSaleById(Long id) {
        log.info("BIKE_SALE_GET - Fetching bike sale with ID: {}", id);

        try {
            BikeSale bikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("BIKE_SALE_GET_FAILED - Bike sale not found with ID: {}", id);
                        return new RuntimeException("BikeSale not found with id: " + id);
                    });

            BikeSaleDTO result = bikeSaleMapper.toDTO(bikeSale);
            // ✅ Enrich with public URLs
            enrichWithPublicUrls(result);

            log.info("BIKE_SALE_GET_SUCCESS - Found bike sale: ID={}", id);
            return result;

        } catch (Exception e) {
            log.error("BIKE_SALE_GET_FAILED - Error fetching bike sale ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to get bike sale: " + e.getMessage(), e);
        }
    }

    @Override
    public BikeSaleDTO updateBikeSale(Long id, BikeSaleDTO bikeSaleDTO) {
        log.info("BIKE_SALE_UPDATE - Updating bike sale with ID: {}", id);

        try {
            BikeSale existingBikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("BIKE_SALE_UPDATE_FAILED - Bike sale not found with ID: {}", id);
                        return new RuntimeException("BikeSale not found with id: " + id);
                    });

            BikeSale updatedBikeSale = bikeSaleMapper.toEntity(bikeSaleDTO);
            updatedBikeSale.setId(id);
            updatedBikeSale.setCreatedAt(existingBikeSale.getCreatedAt()); // Preserve creation time
            updatedBikeSale.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            BikeSale savedBikeSale = bikeSaleRepository.save(updatedBikeSale);

            BikeSaleDTO result = bikeSaleMapper.toDTO(savedBikeSale);
            enrichWithPublicUrls(result);

            log.info("BIKE_SALE_UPDATE_SUCCESS - Updated bike sale: ID={}", id);
            return result;

        } catch (Exception e) {
            log.error("BIKE_SALE_UPDATE_FAILED - Error updating bike sale ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update bike sale: " + e.getMessage(), e);
        }
    }

    @Override
    public BikeSellImageDTO updateBikeImages(Long bikeId, BikeSellImageDTO bikeSellImageDTO) {
        log.info("BIKE_SALE_IMAGES_UPDATE - Updating bike images for bike ID: {}", bikeId);

        try {
            BikeSellImage existingBikeSellImage = bikeSellImageRepository.findByBikeSaleId(bikeId);

            if (existingBikeSellImage == null) {
                existingBikeSellImage = new BikeSellImage();
                existingBikeSellImage.setBikeSale(
                        bikeSaleRepository.findById(bikeId)
                                .orElseThrow(() -> new RuntimeException("BikeSale not found with id: " + bikeId))
                );
            }

            BikeSellImage updatedBikeSellImage = bikeSellImageMapper.toEntity(bikeSellImageDTO);
            updatedBikeSellImage.setId(existingBikeSellImage.getId());
            updatedBikeSellImage.setBikeSale(existingBikeSellImage.getBikeSale());
            updatedBikeSellImage.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            BikeSellImage savedBikeSellImage = bikeSellImageRepository.save(updatedBikeSellImage);

            BikeSellImageDTO result = bikeSellImageMapper.toDTO(savedBikeSellImage);
            // ✅ Enrich with public URLs
            enrichImageDtoWithPublicUrls(result);

            log.info("BIKE_SALE_IMAGES_UPDATE_SUCCESS - Updated images for bike ID: {}", bikeId);
            return result;

        } catch (Exception e) {
            log.error("BIKE_SALE_IMAGES_UPDATE_FAILED - Error updating images for bike ID {}: {}",
                    bikeId, e.getMessage(), e);
            throw new RuntimeException("Failed to update bike images: " + e.getMessage(), e);
        }
    }

    @Override
    public BikeSaleDTO saveBikeSale(BikeSaleDTO bikeSaleDTO) {
        log.info("BIKE_SALE_CREATE - Creating new bike sale");

        try {
            BikeSale bikeSale = bikeSaleMapper.toEntity(bikeSaleDTO);
            bikeSale.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            bikeSale.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            BikeSale saved = bikeSaleRepository.save(bikeSale);

            BikeSaleDTO result = bikeSaleMapper.toDTO(saved);
            enrichWithPublicUrls(result);

            log.info("BIKE_SALE_CREATE_SUCCESS - Created bike sale with ID: {}", saved.getId());
            return result;

        } catch (Exception e) {
            log.error("BIKE_SALE_CREATE_FAILED - Error creating bike sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save bike sale: " + e.getMessage(), e);
        }
    }

    // ✅ NEW: Create bike sale with image uploads
    public BikeSaleDTO saveBikeSaleWithImages(BikeSaleDTO bikeSaleDTO,
                                              MultipartFile pucImage,
                                              MultipartFile insuranceImage,
                                              MultipartFile documentImage,
                                              List<MultipartFile> bikeImages) throws IOException {
        log.info("BIKE_SALE_CREATE_WITH_IMAGES - Creating bike sale with images");

        try {
            BikeSale bikeSale = bikeSaleMapper.toEntity(bikeSaleDTO);

            // ✅ Handle single document images
            if (isValidImageFile(pucImage)) {
                String pucPath = imageUtils.storeBikeSaleImage(pucImage);
                bikeSale.setPucImage(pucPath);
                log.debug("BIKE_SALE_CREATE_WITH_IMAGES - Stored PUC image: {}", pucPath);
            }

            if (isValidImageFile(insuranceImage)) {
                String insurancePath = imageUtils.storeBikeSaleImage(insuranceImage);
                bikeSale.setInsuranceImage(insurancePath);
                log.debug("BIKE_SALE_CREATE_WITH_IMAGES - Stored insurance image: {}", insurancePath);
            }

            if (isValidImageFile(documentImage)) {
                String documentPath = imageUtils.storeBikeSaleImage(documentImage);
                bikeSale.setDocumentImage(documentPath);
                log.debug("BIKE_SALE_CREATE_WITH_IMAGES - Stored document image: {}", documentPath);
            }

            bikeSale.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            bikeSale.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            BikeSale saved = bikeSaleRepository.save(bikeSale);

            // ✅ Handle multiple bike images if provided
            if (bikeImages != null && !bikeImages.isEmpty()) {
                handleMultipleBikeImages(saved, bikeImages);
            }

            BikeSaleDTO result = bikeSaleMapper.toDTO(saved);
            enrichWithPublicUrls(result);

            log.info("BIKE_SALE_CREATE_WITH_IMAGES_SUCCESS - Created bike sale with ID: {}", saved.getId());
            return result;

        } catch (IOException e) {
            log.error("BIKE_SALE_CREATE_WITH_IMAGES_FAILED - Image upload failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("BIKE_SALE_CREATE_WITH_IMAGES_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create bike sale with images: " + e.getMessage(), e);
        }
    }

    // ✅ NEW: Update bike sale with image handling
    public BikeSaleDTO updateBikeSaleWithImages(Long id, BikeSaleDTO bikeSaleDTO,
                                                MultipartFile pucImage,
                                                MultipartFile insuranceImage,
                                                MultipartFile documentImage) throws IOException {
        log.info("BIKE_SALE_UPDATE_WITH_IMAGES - Updating bike sale ID: {} with images", id);

        try {
            BikeSale existingBikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("BikeSale not found with id: " + id));

            // ✅ Handle image updates with cleanup
            if (isValidImageFile(pucImage)) {
                deleteImageSafely(existingBikeSale.getPucImage());
                String newPucPath = imageUtils.storeBikeSaleImage(pucImage);
                existingBikeSale.setPucImage(newPucPath);
                log.debug("BIKE_SALE_UPDATE_WITH_IMAGES - Updated PUC image: {}", newPucPath);
            }

            if (isValidImageFile(insuranceImage)) {
                deleteImageSafely(existingBikeSale.getInsuranceImage());
                String newInsurancePath = imageUtils.storeBikeSaleImage(insuranceImage);
                existingBikeSale.setInsuranceImage(newInsurancePath);
                log.debug("BIKE_SALE_UPDATE_WITH_IMAGES - Updated insurance image: {}", newInsurancePath);
            }

            if (isValidImageFile(documentImage)) {
                deleteImageSafely(existingBikeSale.getDocumentImage());
                String newDocumentPath = imageUtils.storeBikeSaleImage(documentImage);
                existingBikeSale.setDocumentImage(newDocumentPath);
                log.debug("BIKE_SALE_UPDATE_WITH_IMAGES - Updated document image: {}", newDocumentPath);
            }

            // Update other fields from DTO
            BikeSale updatedFields = bikeSaleMapper.toEntity(bikeSaleDTO);
            existingBikeSale.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            // Copy non-image fields (implement based on your BikeSale entity structure)
            copyNonImageFields(updatedFields, existingBikeSale);

            BikeSale saved = bikeSaleRepository.save(existingBikeSale);

            BikeSaleDTO result = bikeSaleMapper.toDTO(saved);
            enrichWithPublicUrls(result);

            log.info("BIKE_SALE_UPDATE_WITH_IMAGES_SUCCESS - Updated bike sale ID: {}", id);
            return result;

        } catch (IOException e) {
            log.error("BIKE_SALE_UPDATE_WITH_IMAGES_FAILED - Image processing failed for ID {}: {}",
                    id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("BIKE_SALE_UPDATE_WITH_IMAGES_FAILED - Error for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update bike sale with images: " + e.getMessage(), e);
        }
    }

    // ✅ NEW: Delete bike sale with image cleanup
    public void deleteBikeSale(Long id) {
        log.info("BIKE_SALE_DELETE - Deleting bike sale with ID: {}", id);

        try {
            BikeSale bikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("BikeSale not found with id: " + id));

            // ✅ Delete all associated images
            deleteAllBikeSaleImages(bikeSale);

            // ✅ Delete associated bike sell images
            BikeSellImage bikeSellImage = bikeSellImageRepository.findByBikeSaleId(id);
            if (bikeSellImage != null) {
                deleteAllBikeSellImages(bikeSellImage);
                bikeSellImageRepository.delete(bikeSellImage);
            }

            // ✅ Delete bike sale record
            bikeSaleRepository.delete(bikeSale);

            log.info("BIKE_SALE_DELETE_SUCCESS - Deleted bike sale with ID: {}", id);

        } catch (Exception e) {
            log.error("BIKE_SALE_DELETE_FAILED - Error deleting bike sale ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete bike sale: " + e.getMessage(), e);
        }
    }

    // ✅ Private helper methods

    private void handleMultipleBikeImages(BikeSale bikeSale, List<MultipartFile> bikeImages) throws IOException {
        BikeSellImage bikeSellImage = new BikeSellImage();
        bikeSellImage.setBikeSale(bikeSale);
        bikeSellImage.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bikeSellImage.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // Process and store images based on position or type
        for (int i = 0; i < bikeImages.size() && i < 4; i++) {
            MultipartFile file = bikeImages.get(i);
            if (isValidImageFile(file)) {
                String imagePath = imageUtils.storeBikeSaleImage(file);

                // Assign images to different positions (front, back, left, right)
                switch (i) {
                    case 0: bikeSellImage.setFrontImages(imagePath); break;
                    case 1: bikeSellImage.setBackImages(imagePath); break;
                    case 2: bikeSellImage.setLeftImages(imagePath); break;
                    case 3: bikeSellImage.setRightImages(imagePath); break;
                }
            }
        }

        bikeSellImageRepository.save(bikeSellImage);
    }

    private void enrichWithPublicUrls(BikeSaleDTO dto) {
        if (dto.getPucImage() != null) {
            dto.setPucImage(imageUtils.getPublicUrl(dto.getPucImage()));
        }
        if (dto.getInsuranceImage() != null) {
            dto.setInsuranceImage(imageUtils.getPublicUrl(dto.getInsuranceImage()));
        }
        if (dto.getDocumentImage() != null) {
            dto.setDocumentImage(imageUtils.getPublicUrl(dto.getDocumentImage()));
        }
    }

    private void enrichImageDtoWithPublicUrls(BikeSellImageDTO dto) {
        if (dto.getFrontImages() != null) {
            dto.setFrontImages(imageUtils.getPublicUrl(dto.getFrontImages()));
        }
        if (dto.getBackImages() != null) {
            dto.setBackImages(imageUtils.getPublicUrl(dto.getBackImages()));
        }
        if (dto.getLeftImages() != null) {
            dto.setLeftImages(imageUtils.getPublicUrl(dto.getLeftImages()));
        }
        if (dto.getRightImages() != null) {
            dto.setRightImages(imageUtils.getPublicUrl(dto.getRightImages()));
        }
    }

    private void deleteAllBikeSaleImages(BikeSale bikeSale) {
        deleteImageSafely(bikeSale.getPucImage());
        deleteImageSafely(bikeSale.getInsuranceImage());
        deleteImageSafely(bikeSale.getDocumentImage());
    }

    private void deleteAllBikeSellImages(BikeSellImage bikeSellImage) {
        deleteImageSafely(bikeSellImage.getFrontImages());
        deleteImageSafely(bikeSellImage.getBackImages());
        deleteImageSafely(bikeSellImage.getLeftImages());
        deleteImageSafely(bikeSellImage.getRightImages());
    }

    private void deleteImageSafely(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                boolean deleted = imageUtils.deleteImage(imagePath);
                if (deleted) {
                    log.debug("BIKE_SALE_IMAGE_DELETE - Successfully deleted image: {}", imagePath);
                } else {
                    log.warn("BIKE_SALE_IMAGE_DELETE - Image not found or already deleted: {}", imagePath);
                }
            } catch (Exception e) {
                log.error("BIKE_SALE_IMAGE_DELETE_FAILED - Failed to delete image {}: {}",
                        imagePath, e.getMessage(), e);
            }
        }
    }

    private boolean isValidImageFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private void copyNonImageFields(BikeSale source, BikeSale target) {
        // Get list of null properties to ignore
        String[] nullProperties = getNullPropertyNames(source);

        // Add image fields to ignore list (don't copy images)
        Set<String> ignoreProperties = new HashSet<>(Arrays.asList(nullProperties));
        ignoreProperties.add("pucImage");
        ignoreProperties.add("insuranceImage");
        ignoreProperties.add("documentImage");
        ignoreProperties.add("id");
        ignoreProperties.add("createdAt");
        ignoreProperties.add("deletedAt");

        // Copy properties while ignoring null and image fields
        BeanUtils.copyProperties(source, target, ignoreProperties.toArray(new String[0]));
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }

}
