package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.dtos.BikeSellRequestDTO;
import com.eptiq.vegobike.services.BikeSaleService;
import com.eptiq.vegobike.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bike-sales")
public class BikeSaleController {

    private static final Logger logger = LoggerFactory.getLogger(BikeSaleController.class);
    private final BikeSaleService bikeSaleService;
    private final ImageUtils imageUtils;

    public BikeSaleController(BikeSaleService bikeSaleService, ImageUtils imageUtils) {
        this.bikeSaleService = bikeSaleService;
        this.imageUtils = imageUtils;
    }

    @GetMapping("adminuser/{id}")
    public ResponseEntity<Map<String, Object>> getBikeSale(@PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to get bike sale with ID: {} (CorrelationId: {})", id, correlationId);
        try {
            Map<String, Object> response = bikeSaleService.getBikeSaleById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", response,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while fetching bike sale with ID: {} (CorrelationId: {})", id, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_FETCH_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }


    @PostMapping(value = "/{bikeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateBikeImages(
            @PathVariable Long bikeId,
            @RequestPart(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage,
            @RequestPart(value = "leftImage", required = false) MultipartFile leftImage,
            @RequestPart(value = "rightImage", required = false) MultipartFile rightImage) {

        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to update bike images for bike ID: {} (CorrelationId: {})", bikeId, correlationId);

        try {
            // Create BikeSellImageDTO and set the MultipartFile objects
            BikeSellImageDTO bikeSellImageDTO = new BikeSellImageDTO();
            bikeSellImageDTO.setFrontImageFile(frontImage);
            bikeSellImageDTO.setBackImageFile(backImage);
            bikeSellImageDTO.setLeftImageFile(leftImage);
            bikeSellImageDTO.setRightImageFile(rightImage);

            // Call the service
            BikeSellImageDTO updatedBikeImageDTO = bikeSaleService.updateBikeImages(bikeId, bikeSellImageDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedBikeImageDTO,
                    "message", "Bike images updated successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while updating images for bike ID: {} (CorrelationId: {})", bikeId, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_IMAGES_UPDATE_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/AdminGetAll")
    public ResponseEntity<Map<String, Object>> getAllBikeSales() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to get all bike sales (CorrelationId: {})", correlationId);
        try {
            List<BikeSaleDTO> bikeSales = bikeSaleService.getAllBikeSales();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", bikeSales,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while fetching all bike sales (CorrelationId: {})", correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKES_FETCH_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @DeleteMapping("Admin/{id}")
    public ResponseEntity<Map<String, Object>> deleteBikeSale(@PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to delete bike sale with ID: {} (CorrelationId: {})", id, correlationId);
        try {
            bikeSaleService.deleteBikeSale(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bike sale deleted successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while deleting bike sale with ID: {} (CorrelationId: {})", id, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_DELETE_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }



    @PutMapping(value = "/{id}/admin-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateBikeSaleAdmin(
            @PathVariable Long id,
            @RequestPart("bikeSaleDTO") BikeSaleDTO bikeSaleDTO,
            @RequestPart(value = "puc_image", required = false) MultipartFile pucImage,
            @RequestPart(value = "document_image", required = false) MultipartFile documentImage
    ) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to update bike sale by admin (ID: {}, CorrelationId: {})", id, correlationId);

        try {
            BikeSaleDTO updatedBikeSale = bikeSaleService.updateBikeSaleAdmin(id, bikeSaleDTO, pucImage, documentImage);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedBikeSale,
                    "message", "Bike sale updated successfully by admin",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (IOException e) {
            logger.error("IO Error while updating bike sale (ID: {}, CorrelationId: {})", id, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ADMIN_BIKE_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("Error while updating bike sale (ID: {}, CorrelationId: {})", id, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ADMIN_BIKE_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }
    @DeleteMapping("/{bikeId}/images/{imageType}")
    public ResponseEntity<Map<String, Object>> deleteBikeImage(
            @PathVariable Long bikeId,
            @PathVariable String imageType) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to delete {} image for bike ID: {} (CorrelationId: {})", imageType, bikeId, correlationId);
        try {
            bikeSaleService.deleteSpecificBikeImage(bikeId, imageType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", String.format("%s image deleted successfully", imageType),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while deleting {} image for bike ID: {} (CorrelationId: {})", imageType, bikeId, correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "IMAGE_DELETE_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }
//    @GetMapping("/user")
//    public ResponseEntity<Map<String, Object>> getAllListedBikeSales() {
//        String correlationId = UUID.randomUUID().toString().substring(0, 8);
//        logger.info("Received request to get all listed bike sales (CorrelationId: {})", correlationId);
//        try {
//            List<BikeSaleDTO> bikeSales = bikeSaleService.getAllListedBikeSales(); // Make sure this matches the method name
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "data", bikeSales,
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        } catch (Exception ex) {
//            logger.error("Error while fetching all listed bike sales (CorrelationId: {})", correlationId, ex);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "success", false,
//                    "error", "BIKES_FETCH_FAILED",
//                    "message", ex.getMessage(),
//                    "timestamp", LocalDateTime.now(),
//                    "correlationId", correlationId
//            ));
//        }
//    }


    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getAllListedBikeSales() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received request to get all listed bike sales with images (CorrelationId: {})", correlationId);
        try {
            List<Map<String, Object>> bikeSalesWithImages = bikeSaleService.getAllListedBikeSalesWithImages();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", bikeSalesWithImages,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception ex) {
            logger.error("Error while fetching all listed bike sales with images (CorrelationId: {})", correlationId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKES_FETCH_FAILED",
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }



    @PostMapping(value = "/sell", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sellBike(
            @RequestPart("requestDTO") BikeSellRequestDTO requestDTO,
            @RequestPart(value = "front_image", required = false) MultipartFile frontImage,
            @RequestPart(value = "back_image", required = false) MultipartFile backImage,
            @RequestPart(value = "left_image", required = false) MultipartFile leftImage,
            @RequestPart(value = "right_image", required = false) MultipartFile rightImage) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.info("Received Sell Bike Request (CorrelationId: {})", correlationId);
        logger.info("Request DTO: {}", requestDTO); // <-- Add this line
        try {
            // Set images in requestDTO
            if (frontImage != null) requestDTO.getBikeSale().setFront_image(frontImage);
            if (backImage != null) requestDTO.getBikeSale().setBack_image(backImage);
            if (leftImage != null) requestDTO.getBikeSale().setLeft_image(leftImage);
            if (rightImage != null) requestDTO.getBikeSale().setRight_image(rightImage);

            bikeSaleService.sellBike(requestDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bike sale request submitted successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "BIKE_SELL_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }
}




