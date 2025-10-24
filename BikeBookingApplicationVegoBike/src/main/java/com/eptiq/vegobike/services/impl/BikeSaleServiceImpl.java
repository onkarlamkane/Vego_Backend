package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.dtos.BikeSellRequestDTO;
import com.eptiq.vegobike.enums.OwnerType;
import com.eptiq.vegobike.mappers.BikeSaleMapper;
import com.eptiq.vegobike.mappers.BikeSellImageMapper;
import com.eptiq.vegobike.mappers.BikeSellerDetailMapper;
import com.eptiq.vegobike.model.BikeSale;
import com.eptiq.vegobike.model.BikeSellImage;
import com.eptiq.vegobike.model.BikeSellerDetail;
import com.eptiq.vegobike.repositories.BikeSaleRepository;
import com.eptiq.vegobike.repositories.BikeSellImageRepository;
import com.eptiq.vegobike.repositories.BikeSellerDetailRepository;
import com.eptiq.vegobike.services.BikeSaleService;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class BikeSaleServiceImpl implements BikeSaleService {

    private final BikeSaleRepository bikeSaleRepository;
    private final BikeSellImageRepository bikeSellImageRepository;
    private final BikeSaleMapper bikeSaleMapper;
    private final BikeSellImageMapper bikeSellImageMapper;
    private final BikeSellerDetailMapper bikeSellerDetailMapper;
    private final BikeSellerDetailRepository bikeSellerDetailRepository;
    private final ImageUtils imageUtils;

    public BikeSaleServiceImpl(BikeSaleRepository bikeSaleRepository,
                               BikeSellImageRepository bikeSellImageRepository,
                               BikeSaleMapper bikeSaleMapper,
                               BikeSellImageMapper bikeSellImageMapper,
                               BikeSellerDetailRepository bikeSellerDetailRepository,
                               BikeSellerDetailMapper bikeSellerDetailMapper,
                               ImageUtils imageUtils) {
        this.bikeSaleRepository = bikeSaleRepository;
        this.bikeSellImageRepository = bikeSellImageRepository;
        this.bikeSaleMapper = bikeSaleMapper;
        this.bikeSellImageMapper = bikeSellImageMapper;
        this.imageUtils = imageUtils;
        this.bikeSellerDetailMapper = bikeSellerDetailMapper;
        this.bikeSellerDetailRepository = bikeSellerDetailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBikeSaleById(Long id) {
        log.info("Fetching bike sale with ID: {}", id);
        try {
            BikeSale bikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Bike sale not found with ID: {}", id);
                        return new RuntimeException("BikeSale not found with id: " + id);
                    });

            // ✅ FIXED: Convert Long to int using Math.toIntExact
            BikeSellerDetail sellerDetail = bikeSellerDetailRepository.findByBikeId(Math.toIntExact(id));
            BikeSellImage bikeSellImage = bikeSellImageRepository.findByBikeSaleId(id);

            BikeSaleDTO bikeSaleDTO = bikeSaleMapper.toDTO(bikeSale, sellerDetail);

            // Use BikeSellImageMapper to map BikeSellImage to BikeSellImageDTO
            BikeSellImageDTO bikeSellImageDTO = bikeSellImageMapper.toDTO(bikeSellImage);

            Map<String, Object> response = new HashMap<>();
            response.put("bikeSale", bikeSaleDTO);
            response.put("bikeImages", bikeSellImageDTO);

            log.info("Successfully fetched bike sale: ID={}", id);
            return response;
        } catch (Exception e) {
            log.error("Error fetching bike sale ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to get bike sale: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BikeSaleDTO> getAllBikeSales() {
        log.info("BIKE_SALE_GET_ALL - Fetching all bike sales");

        List<BikeSale> bikeSales = bikeSaleRepository.findAll();

        return bikeSales.stream()
                .map(bikeSale -> {
                    BikeSaleDTO dto = bikeSaleMapper.toDTO(bikeSale);

                    // ✅ FIXED: Convert Long to int using Math.toIntExact
                    BikeSellerDetail seller = bikeSellerDetailRepository.findByBikeId(Math.toIntExact(bikeSale.getId()));
                    if (seller != null) {
                        dto.setName(seller.getName());
                        dto.setEmail(seller.getEmail());
                        dto.setContactNumber(seller.getContactNumber());
                        dto.setAlternateContactNumber(seller.getAlternateContactNumber());
                        dto.setCity(seller.getCity());
                        dto.setPincode(seller.getPincode());
                        dto.setAddress(seller.getAddress());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BikeSellImageDTO updateBikeImages(Long bikeId, BikeSellImageDTO bikeSellImageDTO) {
        log.info("Updating bike images for bike ID: {}", bikeId);

        BikeSale bikeSale = bikeSaleRepository.findById(bikeId)
                .orElseThrow(() -> new RuntimeException("BikeSale not found with ID: " + bikeId));

        BikeSellImage existingBikeSellImage = bikeSellImageRepository.findByBikeSaleId(bikeId);
        if (existingBikeSellImage == null) {
            existingBikeSellImage = new BikeSellImage();
            existingBikeSellImage.setBikeSale(bikeSale);
            log.info("Created new BikeSellImage for BikeSale ID: {}", bikeId);
        }

        try {
            // Front Image
            if (existingBikeSellImage.getFrontImages() != null) {
                imageUtils.deleteImage(existingBikeSellImage.getFrontImages());
            }
            if (bikeSellImageDTO.getFrontImageFile() != null && !bikeSellImageDTO.getFrontImageFile().isEmpty()) {
                String path = imageUtils.storeBikeSaleImage(bikeSellImageDTO.getFrontImageFile());
                existingBikeSellImage.setFrontImages(path);
            }

            if (existingBikeSellImage.getBackImages() != null) {
                imageUtils.deleteImage(existingBikeSellImage.getBackImages());
            }
            if (bikeSellImageDTO.getBackImageFile() != null && !bikeSellImageDTO.getBackImageFile().isEmpty()) {
                String path = imageUtils.storeBikeSaleImage(bikeSellImageDTO.getBackImageFile());
                existingBikeSellImage.setBackImages(path);
            }

            if (existingBikeSellImage.getLeftImages() != null) {
                imageUtils.deleteImage(existingBikeSellImage.getLeftImages());
            }
            if (bikeSellImageDTO.getLeftImageFile() != null && !bikeSellImageDTO.getLeftImageFile().isEmpty()) {
                String path = imageUtils.storeBikeSaleImage(bikeSellImageDTO.getLeftImageFile());
                existingBikeSellImage.setLeftImages(path);
            }

            if (existingBikeSellImage.getRightImages() != null) {
                imageUtils.deleteImage(existingBikeSellImage.getRightImages());
            }
            if (bikeSellImageDTO.getRightImageFile() != null && !bikeSellImageDTO.getRightImageFile().isEmpty()) {
                String path = imageUtils.storeBikeSaleImage(bikeSellImageDTO.getRightImageFile());
                existingBikeSellImage.setRightImages(path);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage(), e);
        }

        existingBikeSellImage.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        BikeSellImage saved = bikeSellImageRepository.save(existingBikeSellImage);
        return bikeSellImageMapper.toDTO(saved);
    }

    // ✅ NEW: Delete bike sale with image cleanup
    public void deleteBikeSale(Long id) {
        log.info("BIKE_SALE_DELETE - Deleting bike sale with ID: {}", id);
        try {
            BikeSale bikeSale = bikeSaleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("BikeSale not found with id: " + id));

            // Delete associated bike sell images
            BikeSellImage bikeSellImage = bikeSellImageRepository.findByBikeSaleId(id);
            if (bikeSellImage != null) {
                deleteAllBikeSellImages(bikeSellImage);
                bikeSellImageRepository.delete(bikeSellImage);
            }

            // Delete bike sale record
            bikeSaleRepository.delete(bikeSale);
            log.info("BIKE_SALE_DELETE_SUCCESS - Deleted bike sale with ID: {}", id);
        } catch (Exception e) {
            log.error("BIKE_SALE_DELETE_FAILED - Error deleting bike sale ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete bike sale: " + e.getMessage(), e);
        }
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

    @Override
    @Transactional
    public void sellBike(BikeSellRequestDTO requestDTO) throws IOException {
        log.info("BIKE_SALE_SELL - Processing sellBike request");
        // 1️⃣ Map DTO to entity
        BikeSaleDTO bikeSaleDTO = requestDTO.getBikeSale();
        if (bikeSaleDTO == null) {
            throw new IllegalArgumentException("BikeSaleDTO cannot be null");
        }
        BikeSale bikeSale = bikeSaleMapper.toEntity(bikeSaleDTO);
        // ✅ Mandatory checks for BikeSale
        if (bikeSale.getBrandId() == null) throw new IllegalArgumentException("brandId is required");
        if (bikeSale.getModelId() == null) throw new IllegalArgumentException("modelId is required");
        if (bikeSale.getCategoryId() == null) throw new IllegalArgumentException("categoryId is required");
        if (bikeSale.getYearId() == null) throw new IllegalArgumentException("yearId is required");
        if (bikeSale.getRegistrationNumber() == null || bikeSale.getRegistrationNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("registrationNumber is required");
        }
        if (bikeSale.getColor() == null || bikeSale.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("color is required");
        }
        if (bikeSaleDTO.getNumberOfOwner() == null) {
            throw new IllegalArgumentException("numberOfOwner is required");
        }
        // ✅ Convert int to OwnerType for validation and readability
        OwnerType ownerType = OwnerType.fromValue(bikeSaleDTO.getNumberOfOwner());
        bikeSale.setNumberOfOwner(bikeSaleDTO.getNumberOfOwner()); // Store int in DB

        if (bikeSaleDTO.getSellingPrice() != null) {
            bikeSale.setSellingPrice(bikeSaleDTO.getSellingPrice());
            bikeSale.setCustomerSellingClosingPrice(bikeSaleDTO.getSellingPrice());
            bikeSale.setSellingClosingPrice(bikeSaleDTO.getSellingPrice());
        } else {
            throw new IllegalArgumentException("sellingPrice is required");
        }
        // ✅ kmsDriven required check
        if (bikeSale.getKmsDriven() == null || bikeSale.getKmsDriven() <= 0) {
            throw new IllegalArgumentException("kmsDriven is required and must be greater than 0");
        }
        if (bikeSaleDTO.getBikeCondition() != null) {
            bikeSale.setBikeCondition(String.valueOf(bikeSaleDTO.getBikeCondition()));
        } else {
            throw new IllegalArgumentException("bikeCondition is required");
        }
        // ✅ Default values for optional numeric fields
        if (bikeSale.getKmsDriven() == null) bikeSale.setKmsDriven(0);
        bikeSale.setSellId(UUID.randomUUID().toString());
        bikeSale.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bikeSale.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // ✅ Mandatory check for all 4 images
        if (bikeSaleDTO.getFront_image() == null || bikeSaleDTO.getFront_image().isEmpty()) {
            throw new IllegalArgumentException("Front image is required");
        }
        if (bikeSaleDTO.getBack_image() == null || bikeSaleDTO.getBack_image().isEmpty()) {
            throw new IllegalArgumentException("Back image is required");
        }
        if (bikeSaleDTO.getLeft_image() == null || bikeSaleDTO.getLeft_image().isEmpty()) {
            throw new IllegalArgumentException("Left image is required");
        }
        if (bikeSaleDTO.getRight_image() == null || bikeSaleDTO.getRight_image().isEmpty()) {
            throw new IllegalArgumentException("Right image is required");
        }

        // 2️⃣ Save BikeSale
        BikeSale savedBikeSale = bikeSaleRepository.save(bikeSale);

        // 3️⃣ Save Seller Details
        BikeSellerDetail sellerDetail = bikeSellerDetailMapper.toEntity(requestDTO.getSellerDetail());
        if (sellerDetail != null) {
            if (sellerDetail.getContactNumber() == null || sellerDetail.getContactNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("contactNumber is required");
            }
            if (sellerDetail.getName() == null || sellerDetail.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("name is required");
            }
            if (sellerDetail.getAddress() == null || sellerDetail.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("address is required");
            }
            if (sellerDetail.getCity() == null || sellerDetail.getCity().trim().isEmpty()) {
                throw new IllegalArgumentException("city is required");
            }
            if (sellerDetail.getPincode() == null || sellerDetail.getPincode().trim().isEmpty()) {
                throw new IllegalArgumentException("pincode is required");
            }
            if (!sellerDetail.getPincode().matches("^[1-9][0-9]{5}$")) {
                throw new IllegalArgumentException("Invalid pincode (must be 6 digits and not start with 0)");
            }

            // ✅ FIXED: Convert Long to int using Math.toIntExact
            sellerDetail.setBikeId(Math.toIntExact(savedBikeSale.getId()));
            sellerDetail.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            sellerDetail.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            bikeSellerDetailRepository.save(sellerDetail);
        }

        // 4️⃣ Save BikeSellImage
        BikeSellImage bikeSellImage = new BikeSellImage();
        bikeSellImage.setBikeSale(savedBikeSale);
        bikeSellImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bikeSellImage.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        bikeSellImage.setFrontImages(imageUtils.storeBikeSaleImage(bikeSaleDTO.getFront_image()));
        bikeSellImage.setBackImages(imageUtils.storeBikeSaleImage(bikeSaleDTO.getBack_image()));
        bikeSellImage.setLeftImages(imageUtils.storeBikeSaleImage(bikeSaleDTO.getLeft_image()));
        bikeSellImage.setRightImages(imageUtils.storeBikeSaleImage(bikeSaleDTO.getRight_image()));
        bikeSellImageRepository.save(bikeSellImage);
        log.info("BIKE_SALE_SELL_SUCCESS - SellBike completed, ID: {}", savedBikeSale.getId());
    }

    @Override
    public void deleteSpecificBikeImage(Long bikeId, String imageType) throws Exception {
        log.info("Deleting {} image for bike ID: {}", imageType, bikeId);
        BikeSellImage bikeSellImage = bikeSellImageRepository.findByBikeSaleId(bikeId);
        if (bikeSellImage == null) {
            throw new RuntimeException("No images found for bike ID: " + bikeId);
        }

        String imagePath = null;
        switch (imageType.toLowerCase()) {
            case "front":
                imagePath = bikeSellImage.getFrontImages();
                bikeSellImage.setFrontImages(null);
                break;
            case "back":
                imagePath = bikeSellImage.getBackImages();
                bikeSellImage.setBackImages(null);
                break;
            case "left":
                imagePath = bikeSellImage.getLeftImages();
                bikeSellImage.setLeftImages(null);
                break;
            case "right":
                imagePath = bikeSellImage.getRightImages();
                bikeSellImage.setRightImages(null);
                break;
            default:
                throw new IllegalArgumentException("Invalid image type: " + imageType);
        }

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            boolean deleted = imageUtils.deleteImage(imagePath);
            if (!deleted) {
                log.warn("Image not found or already deleted: {}", imagePath);
            }
        }

        bikeSellImage.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bikeSellImageRepository.save(bikeSellImage);
        log.info("Successfully deleted {} image for bike ID: {}", imageType, bikeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BikeSaleDTO> getAllListedBikeSales() {
        log.info("BIKE_SALE_GET_ALL_LISTED - Fetching all listed bike sales");
        List<BikeSale> bikeSales = bikeSaleRepository.findByStatus("LISTED");
        return bikeSales.stream()
                .map(bikeSale -> {
                    BikeSaleDTO dto = bikeSaleMapper.toDTO(bikeSale);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllListedBikeSalesWithImages() {
        log.info("BIKE_SALE_GET_ALL_LISTED_WITH_IMAGES - Fetching all listed bike sales with images and seller details");

        List<BikeSale> bikeSales = bikeSaleRepository.findByStatus("LISTED");

        return bikeSales.stream()
                .map(bikeSale -> {
                    // Create bike sale DTO with all existing fields
                    BikeSaleDTO dto = bikeSaleMapper.toDTO(bikeSale);

                    // ✅ ADD seller details to existing DTO (without removing any existing fields)
                    BikeSellerDetail sellerDetail = bikeSellerDetailRepository.findByBikeId(Math.toIntExact(bikeSale.getId()));
                    if (sellerDetail != null) {
                        dto.setName(sellerDetail.getName());
                        dto.setEmail(sellerDetail.getEmail());
                        dto.setContactNumber(sellerDetail.getContactNumber());
                        dto.setAlternateContactNumber(sellerDetail.getAlternateContactNumber());
                        dto.setCity(sellerDetail.getCity());
                        dto.setPincode(sellerDetail.getPincode());
                        dto.setAddress(sellerDetail.getAddress());
                    }

                    // Get bike images (keeping existing structure)
                    BikeSellImage bikeSellImage = bikeSellImageRepository.findByBikeSaleId(bikeSale.getId());
                    BikeSellImageDTO imageDTO = null;
                    if (bikeSellImage != null) {
                        imageDTO = bikeSellImageMapper.toDTO(bikeSellImage);
                    }

                    // Create response map (keeping existing structure)
                    Map<String, Object> bikeWithImages = new HashMap<>();
                    bikeWithImages.put("bikeSale", dto);
                    bikeWithImages.put("images", imageDTO); // Keeping "images" as in your original code

                    return bikeWithImages;
                })
                .collect(Collectors.toList());
    }


    @Override
    public BikeSaleDTO updateBikeSaleAdmin(
            Long id,
            BikeSaleDTO bikeSaleDTO,
            MultipartFile pucImage,
            MultipartFile documentImage
    ) throws IOException {
        log.info("ADMIN_BIKE_SALE_UPDATE - Updating bike sale ID: {}", id);

        // 1. Fetch existing bike sale
        BikeSale existingBikeSale = bikeSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BikeSale not found with id: " + id));

        // 2. Update PUC Image and set isPuc flag
        if (isValidImageFile(pucImage)) {
            deleteImageSafely(existingBikeSale.getPucImage());
            existingBikeSale.setPucImage(imageUtils.storeBikeSaleImage(pucImage));
            existingBikeSale.setIsPuc(1); // Set to 1 (true) if PUC image is uploaded
        } else if (pucImage != null && pucImage.isEmpty()) {
            // If an empty PUC image is provided, delete the existing PUC image and set isPuc to 0
            deleteImageSafely(existingBikeSale.getPucImage());
            existingBikeSale.setPucImage(null);
            existingBikeSale.setIsPuc(0); // Set to 0 (false) if PUC image is removed
        }

        // 3. Update Document Image and set isDocument flag
        if (isValidImageFile(documentImage)) {
            deleteImageSafely(existingBikeSale.getDocumentImage());
            existingBikeSale.setDocumentImage(imageUtils.storeBikeSaleImage(documentImage));
            existingBikeSale.setIsDocument(1); // Set to 1 (true) if document image is uploaded
        } else if (documentImage != null && documentImage.isEmpty()) {
            // If an empty document image is provided, delete the existing document image and set isDocument to 0
            deleteImageSafely(existingBikeSale.getDocumentImage());
            existingBikeSale.setDocumentImage(null);
            existingBikeSale.setIsDocument(0); // Set to 0 (false) if document image is removed
        }

        // 4. Update Supervisor Name
        if (bikeSaleDTO.getSupervisorName() != null && !bikeSaleDTO.getSupervisorName().trim().isEmpty()) {
            existingBikeSale.setSupervisorName(bikeSaleDTO.getSupervisorName());
        }


        // 5. Update Bike Condition
        if (bikeSaleDTO.getBikeCondition() != null) {
            existingBikeSale.setBikeCondition(bikeSaleDTO.getBikeCondition().name());
        }


        // 6. Update Additional Notes
        if (bikeSaleDTO.getAdditionalNotes() != null && !bikeSaleDTO.getAdditionalNotes().trim().isEmpty()) {
            existingBikeSale.setAdditionalNotes(bikeSaleDTO.getAdditionalNotes());
        }

        // 7. Update Listing Status (with enum)
        if (bikeSaleDTO.getListingStatus() != null) {
            existingBikeSale.setStatus(bikeSaleDTO.getListingStatus().name());
        }

        // 8. Update Repair Required
        if (bikeSaleDTO.getIsRepairRequired() != null) {
            existingBikeSale.setIsRepairRequired(bikeSaleDTO.getIsRepairRequired() ? 1 : 0);
        }

        // 9. Update Customer Selling Closing Price
        if (bikeSaleDTO.getCustomerSellingClosingPrice() != null) {
            existingBikeSale.setCustomerSellingClosingPrice(
                    BigDecimal.valueOf(bikeSaleDTO.getCustomerSellingClosingPrice())
            );
        }

        // 10. Update timestamp
        existingBikeSale.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // 11. Save and return
        BikeSale savedBikeSale = bikeSaleRepository.save(existingBikeSale);
        BikeSaleDTO result = bikeSaleMapper.toDTO(savedBikeSale);

        log.info("ADMIN_BIKE_SALE_UPDATE_SUCCESS - Updated bike sale ID: {}", id);
        return result;
    }
}
