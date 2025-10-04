//package com.eptiq.vegobike.services.impl;
//
//import com.eptiq.vegobike.dtos.*;
//import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
//import com.eptiq.vegobike.mappers.BikeMapper;
//import com.eptiq.vegobike.model.Bike;
//import com.eptiq.vegobike.model.BikeImage;
//import com.eptiq.vegobike.model.PriceList;
//import com.eptiq.vegobike.repositories.BikeImageRepository;
//import com.eptiq.vegobike.repositories.BikeRepository;
//import com.eptiq.vegobike.repositories.PriceListRepository;
//import com.eptiq.vegobike.services.BikeService;
//
//import com.eptiq.vegobike.utils.ImageUtils;
//import io.lettuce.core.dynamic.annotation.Value;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class BikeServiceImpl implements BikeService {
//
//    private final BikeRepository bikeRepository;
//    private final BikeImageRepository bikeImageRepository;
//    private final ImageUtils imageUtils;
//    private final BikeMapper mapper;
//    private final PriceListRepository priceListRepository;
//
//    @Override
//    public BikeResponseDTO createBike(BikeRequestDTO request) throws IOException {
//        Bike bike = mapper.toEntity(request);
//
//        // Handle single images
//        if (request.getPucImage() != null && !request.getPucImage().isEmpty()) {
//            bike.setPucImage(imageUtils.storeImage(request.getPucImage(), ImageUtils.BIKES_FOLDER));
//        }
//        if (request.getInsuranceImage() != null && !request.getInsuranceImage().isEmpty()) {
//            bike.setInsuranceImage(imageUtils.storeImage(request.getInsuranceImage(), ImageUtils.BIKES_FOLDER));
//        }
//        if (request.getDocumentImage() != null && !request.getDocumentImage().isEmpty()) {
//            bike.setDocumentImage(imageUtils.storeImage(request.getDocumentImage(), ImageUtils.BIKES_FOLDER));
//        }
//
//        bike.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//        Bike savedBike = bikeRepository.save(bike);
//
//        // Handle multiple bike images
//        if (request.getImages() != null && !request.getImages().isEmpty()) {
//            for (MultipartFile file : request.getImages()) {
//                if (!file.isEmpty()) {
//                    String storedPath = imageUtils.storeImage(file, ImageUtils.BIKES_FOLDER);
//                    BikeImage bikeImage = new BikeImage();
//                    bikeImage.setBike(savedBike);
//                    bikeImage.setImages(storedPath);
//                    bikeImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//                    bikeImageRepository.save(bikeImage);
//                }
//            }
//        }
//
//        return BikeMapper.toDTO(savedBike, getBikeImages(savedBike.getId()));
//    }
//
//    @Override
//    public BikeResponseDTO updateBike(int id, BikeRequestDTO request) throws IOException {
//        Bike bike = bikeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + id));
//
//        mapper.updateEntity(request, bike);
//
//        // Replace single images if new ones are uploaded
//        if (request.getPucImage() != null && !request.getPucImage().isEmpty()) {
//            if (bike.getPucImage() != null) imageUtils.deleteImage(bike.getPucImage());
//            bike.setPucImage(imageUtils.storeImage(request.getPucImage(), ImageUtils.BIKES_FOLDER));
//        }
//        if (request.getInsuranceImage() != null && !request.getInsuranceImage().isEmpty()) {
//            if (bike.getInsuranceImage() != null) imageUtils.deleteImage(bike.getInsuranceImage());
//            bike.setInsuranceImage(imageUtils.storeImage(request.getInsuranceImage(), ImageUtils.BIKES_FOLDER));
//        }
//        if (request.getDocumentImage() != null && !request.getDocumentImage().isEmpty()) {
//            if (bike.getDocumentImage() != null) imageUtils.deleteImage(bike.getDocumentImage());
//            bike.setDocumentImage(imageUtils.storeImage(request.getDocumentImage(), ImageUtils.BIKES_FOLDER));
//        }
//
//        bike.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//        Bike updatedBike = bikeRepository.save(bike);
//
//        // Replace multiple bike images if new ones uploaded
//        if (request.getImages() != null && !request.getImages().isEmpty()) {
//            bikeImageRepository.deleteByBike_Id(id);
//
//            for (MultipartFile file : request.getImages()) {
//                if (!file.isEmpty()) {
//                    String storedPath = imageUtils.storeImage(file, ImageUtils.BIKES_FOLDER);
//                    BikeImage bikeImage = new BikeImage();
//                    bikeImage.setBike(updatedBike);
//                    bikeImage.setImages(storedPath);
//                    bikeImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//                    bikeImageRepository.save(bikeImage);
//                }
//            }
//        }
//
//        return BikeMapper.toDTO(updatedBike, getBikeImages(updatedBike.getId()));
//    }
//
//    @Override
//    public List<BikeResponseDTO> getAllBikes() {
//        return bikeRepository.findAll()
//                .stream()
//                .map(bike -> BikeMapper.toDTO(bike, getBikeImages(bike.getId())))
//                .toList();
//    }
//
//    @Override
//    public BikeResponseDTO getBikeById(int id) {
//        Bike bike = bikeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + id));
//        return BikeMapper.toDTO(bike, getBikeImages(id));
//    }
////
////    @Override
////    public void deleteBike(int id) {
////        // remove multiple images
////        bikeImageRepository.deleteByBikeId(id);
////        bikeRepository.deleteById(id);
////    }
//
//    private List<String> getBikeImages(int bikeId) {
//        return bikeImageRepository.findImagePathsByBikeId(bikeId)
//                .stream()
//                .map(imageUtils::getPublicUrlVersioned)
//                .toList();
//    }
//
//
//
////    @Override
////    public Page<AvailableBikeDto> getAvailableBikes(Date startDate, Date endDate,
////                                                    String addressType, String search,
////                                                    Pageable pageable) {
////        List<Integer> activeStatuses = List.of(1, 2, 3, 4);
////
////        Page<AvailableBikeDto> bikes = bikeRepository
////                .findAvailableBikes(search, activeStatuses, startDate, endDate, pageable)
////                .map(mapper::toAvailableBikeDto);
////
////        // Filter by addressType in service layer if needed
////        if (addressType != null && !addressType.isEmpty()) {
////            // This is a simplified approach - you might want to implement
////            // more sophisticated filtering based on your business logic
////            return bikes; // For now, returning all bikes
////        }
////
////        return bikes;
////    }
//
//
//
//    // In BikeServiceImpl
//
//    // In BikeServiceImpl
//
//    // src/main/java/com/eptiq/vegobike/services/impl/BikeServiceImpl.java
//// Inject ImageUtils and remove the old publicBaseUrl/toPublicUrl helpers [web:71]
//    // already present [web:71]
//
//// Mapping the main image in getAvailableBikes(...)
//dto.setMainImageUrl(imageUtils.getPublicUrlVersioned(row.getMainImageUrl())); // versioned URL [web:177][web:80]
//
//                                     // final URLs for clients [web:71]
//    }
//
//
//    @Override
//    @jakarta.transaction.Transactional
//    public Page<AvailableBikeDto> getAvailableBikes(Date startDate, Date endDate,
//                                                    String addressType, String search,
//                                                    Pageable pageable) {
//        List<Integer> activeStatuses = List.of(1, 2, 3, 4);
//
//        Page<AvailableBikeRow> slice = bikeRepository.findAvailableBikeRows(
//                search, activeStatuses, startDate, endDate, pageable); // projection page [web:19][web:15]
//
//        if (slice.isEmpty()) {
//            return new org.springframework.data.domain.PageImpl<>(java.util.List.of(), pageable, 0); // empty page [web:8]
//        }
//
//        // Batch packages by category for this page
//        java.util.Set<Integer> categoryIds = slice.getContent().stream()
//                .map(AvailableBikeRow::getCategoryId).filter(java.util.Objects::nonNull)
//                .collect(java.util.stream.Collectors.toSet()); // collect categories [web:19][web:15]
//
//        java.util.List<com.eptiq.vegobike.model.PriceList> prices =
//                priceListRepository.findByCategoryIdInAndIsActive(categoryIds, 1); // batch fetch [web:36]
//
//        java.util.Map<Integer, java.util.List<com.eptiq.vegobike.dtos.PriceListDTO>> packagesByCategory =
//                prices.stream().collect(java.util.stream.Collectors.groupingBy(
//                        com.eptiq.vegobike.model.PriceList::getCategoryId,
//                        java.util.stream.Collectors.mapping(pl -> {
//                            com.eptiq.vegobike.dtos.PriceListDTO dto = new com.eptiq.vegobike.dtos.PriceListDTO();
//                            dto.setDays(pl.getDays());
//                            dto.setPrice(pl.getPrice());
//                            dto.setDeposit(pl.getDeposit());
//                            dto.setHourlyChargeAmount(pl.getHourlyChargeAmount());
//                            return dto;
//                        }, java.util.stream.Collectors.toList())
//                )); // map packages [web:19][web:15]
//
//        java.util.List<AvailableBikeDto> content = slice.getContent().stream().map(row -> {
//            AvailableBikeDto dto = new AvailableBikeDto();
//            dto.setId(row.getId());
//            dto.setName(row.getName()); // may be null if bikes.name is null [web:36]
//            dto.setCategoryId(row.getCategoryId());
//            dto.setModelId(row.getModelId());
//            dto.setRegistrationNumber(row.getRegistrationNumber());
//            dto.setStoreName(row.getStoreName());
//            dto.setMainImageUrl(toPublicUrl(row.getMainImageUrl())); // build full URL [web:80][web:71]
//            dto.setPackages(packagesByCategory.getOrDefault(row.getCategoryId(), java.util.List.of()));
//            return dto;
//        }).toList(); // projection->DTO mapping [web:19][web:15]
//
//        return new org.springframework.data.domain.PageImpl<>(content, pageable, slice.getTotalElements()); // return page [web:8]
//    }
//
//}



package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.BikeMapper;
import com.eptiq.vegobike.model.Bike;
import com.eptiq.vegobike.model.BikeImage;
import com.eptiq.vegobike.model.PriceList;
import com.eptiq.vegobike.repositories.BikeImageRepository;
import com.eptiq.vegobike.repositories.BikeRepository;
import com.eptiq.vegobike.repositories.PriceListRepository;
import com.eptiq.vegobike.services.BikeService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;
    private final BikeImageRepository bikeImageRepository;
    private final ImageUtils imageUtils;
    private final BikeMapper mapper;
    private final PriceListRepository priceListRepository;

    @Override
    public BikeResponseDTO createBike(BikeRequestDTO request) throws IOException {
        Bike bike = mapper.toEntity(request);

        if (request.getPucImage() != null && !request.getPucImage().isEmpty()) {
            bike.setPucImage(imageUtils.storeImage(request.getPucImage(), ImageUtils.BIKES_FOLDER));
        }
        if (request.getInsuranceImage() != null && !request.getInsuranceImage().isEmpty()) {
            bike.setInsuranceImage(imageUtils.storeImage(request.getInsuranceImage(), ImageUtils.BIKES_FOLDER));
        }
        if (request.getDocumentImage() != null && !request.getDocumentImage().isEmpty()) {
            bike.setDocumentImage(imageUtils.storeImage(request.getDocumentImage(), ImageUtils.BIKES_FOLDER));
        }

        bike.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        Bike savedBike = bikeRepository.save(bike);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            List<BikeImage> toSave = new ArrayList<>();
            for (MultipartFile file : request.getImages()) {
                if (file != null && !file.isEmpty()) {
                    String storedPath = imageUtils.storeImage(file, ImageUtils.BIKES_FOLDER);
                    BikeImage bi = new BikeImage();
                    bi.setBike(savedBike);
                    bi.setImages(storedPath);
                    bi.setCreatedAt(now);
                    bi.setUpdatedAt(now);
                    toSave.add(bi);
                }
            }
            if (!toSave.isEmpty()) {
                bikeImageRepository.saveAll(toSave);
            }
        }

        return BikeMapper.toDTO(savedBike, getBikeImages(savedBike.getId()));
    }

    @Override
    public BikeResponseDTO updateBike(int id, BikeRequestDTO request) throws IOException {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + id));

        mapper.updateEntity(request, bike);

        if (request.getPucImage() != null && !request.getPucImage().isEmpty()) {
            if (bike.getPucImage() != null) imageUtils.deleteImage(bike.getPucImage());
            bike.setPucImage(imageUtils.storeImage(request.getPucImage(), ImageUtils.BIKES_FOLDER));
        }
        if (request.getInsuranceImage() != null && !request.getInsuranceImage().isEmpty()) {
            if (bike.getInsuranceImage() != null) imageUtils.deleteImage(bike.getInsuranceImage());
            bike.setInsuranceImage(imageUtils.storeImage(request.getInsuranceImage(), ImageUtils.BIKES_FOLDER));
        }
        if (request.getDocumentImage() != null && !request.getDocumentImage().isEmpty()) {
            if (bike.getDocumentImage() != null) imageUtils.deleteImage(bike.getDocumentImage());
            bike.setDocumentImage(imageUtils.storeImage(request.getDocumentImage(), ImageUtils.BIKES_FOLDER));
        }

        bike.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        Bike updatedBike = bikeRepository.save(bike);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            bikeImageRepository.deleteByBike_Id(id);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            List<BikeImage> toSave = new ArrayList<>();
            for (MultipartFile file : request.getImages()) {
                if (file != null && !file.isEmpty()) {
                    String storedPath = imageUtils.storeImage(file, ImageUtils.BIKES_FOLDER);
                    BikeImage bi = new BikeImage();
                    bi.setBike(updatedBike);
                    bi.setImages(storedPath);
                    bi.setCreatedAt(now);
                    bi.setUpdatedAt(now);
                    toSave.add(bi);
                }
            }
            if (!toSave.isEmpty()) {
                bikeImageRepository.saveAll(toSave);
            }
        }

        return BikeMapper.toDTO(updatedBike, getBikeImages(updatedBike.getId()));
    }

    @Override
    public List<BikeResponseDTO> getAllBikes() {
        return bikeRepository.findAll()
                .stream()
                .map(bike -> BikeMapper.toDTO(bike, getBikeImages(bike.getId())))
                .toList();
    }

    @Override
    public BikeResponseDTO getBikeById(int id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + id));
        return BikeMapper.toDTO(bike, getBikeImages(id));
    }

//    private List<String> getBikeImages(int bikeId) {
//        return bikeImageRepository.findImagePathsByBikeId(bikeId)
//                .stream()
//                .map(imageUtils::getPublicUrlVersioned)
//                .toList();
//    }

    private List<String> getBikeImages(int bikeId) {
        return bikeImageRepository.findImagePathsByBikeId(bikeId)
                .stream()
                .map(imageUtils::getPublicUrlVersioned)
                .toList();
    }


    @Override
    @Transactional
    public Page<AvailableBikeDto> getAvailableBikes(Date startDate, Date endDate,
                                                    String addressType, String search,
                                                    Pageable pageable) {
        List<Integer> activeStatuses = List.of(1, 2, 3, 4);

        Page<AvailableBikeRow> slice = bikeRepository.findAvailableBikeRows(
                search, activeStatuses, startDate, endDate, pageable);

        if (slice.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        Set<Integer> categoryIds = slice.getContent().stream()
                .map(AvailableBikeRow::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<PriceList> prices = priceListRepository.findByCategoryIdInAndIsActive(categoryIds, 1);

        Map<Integer, List<PriceListDTO>> packagesByCategory = prices.stream()
                .collect(Collectors.groupingBy(
                        PriceList::getCategoryId,
                        Collectors.mapping(pl -> {
                            PriceListDTO dto = new PriceListDTO();
                            dto.setDays(pl.getDays());
                            dto.setPrice(pl.getPrice());
                            dto.setDeposit(pl.getDeposit());
                            dto.setHourlyChargeAmount(pl.getHourlyChargeAmount());
                            return dto;
                        }, Collectors.toList())
                ));

        List<AvailableBikeDto> content = slice.getContent().stream()
                .map(row -> {
                    AvailableBikeDto dto = new AvailableBikeDto();
                    dto.setId(row.getId());
                    dto.setName(row.getName());
                    dto.setCategoryId(row.getCategoryId());
                    dto.setModelId(row.getModelId());
                    dto.setRegistrationNumber(row.getRegistrationNumber());
                    dto.setStoreName(row.getStoreName());
                    dto.setMainImageUrl(imageUtils.getPublicUrlVersioned(row.getMainImageUrl()));
                    dto.setPackages(packagesByCategory.getOrDefault(row.getCategoryId(), List.of()));
                    return dto;
                })
                .toList();

        return new PageImpl<>(content, pageable, slice.getTotalElements());
    }

    @Override
    public BikeDocumentsDTO getBikeDocuments(int bikeId) {
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + bikeId));
        return mapper.toBikeDocumentsDto(bike);

    }
}
