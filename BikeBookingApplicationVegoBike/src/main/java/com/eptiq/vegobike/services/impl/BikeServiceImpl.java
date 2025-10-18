package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.enums.BikeStatus;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.BikeMapper;
import com.eptiq.vegobike.model.Bike;
import com.eptiq.vegobike.model.BikeImage;
import com.eptiq.vegobike.model.PriceList;
import com.eptiq.vegobike.repositories.BikeImageRepository;
import com.eptiq.vegobike.repositories.BikeRepository;
import com.eptiq.vegobike.repositories.PriceListRepository;
import com.eptiq.vegobike.services.BikeService;
import com.eptiq.vegobike.services.BrandService;
import com.eptiq.vegobike.services.CategoryService;
import com.eptiq.vegobike.services.ModelService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ModelService modelService;

    @Override
    public BikeResponseDTO createBike(BikeRequestDTO request) throws IOException {
        Bike bike = mapper.toEntity(request);
        bike.setIsActive(1);
        bike.setBikeStatus(BikeStatus.AVAILABLE);



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

        String brandName = brandService.getById(savedBike.getBrandId()).getBrandName();
        String categoryName = categoryService.getCategoryById(savedBike.getCategoryId()).getCategoryName();
        String modelName = modelService.getById(savedBike.getModelId()).getModelName();
        String status = savedBike.getBikeStatus() != null ? savedBike.getBikeStatus().name() : "UNKNOWN";
        List<String> images = getBikeImages(savedBike.getId());

        return BikeMapper.toDTO(savedBike, brandName, categoryName, modelName, status, images);
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

        String brandName = brandService.getById(updatedBike.getBrandId()).getBrandName();
        String categoryName = categoryService.getCategoryById(updatedBike.getCategoryId()).getCategoryName();
        String modelName = modelService.getById(updatedBike.getModelId()).getModelName();
        String status = updatedBike.getBikeStatus() != null ? updatedBike.getBikeStatus().name() : "UNKNOWN";
        List<String> images = getBikeImages(updatedBike.getId());

        return BikeMapper.toDTO(updatedBike, brandName, categoryName, modelName, status, images);
    }
    @Override
    public Page<BikeResponseDTO> getAllBikes(Pageable pageable) {
        return bikeRepository.findAll(pageable)
                .map(bike -> {
                    String brandName = brandService.getById(bike.getBrandId()).getBrandName();
                    String categoryName = categoryService.getCategoryById(bike.getCategoryId()).getCategoryName();
                    String modelName = modelService.getById(bike.getModelId()).getModelName();
                    String status = bike.getBikeStatus() != null ? bike.getBikeStatus().name() : "UNKNOWN";
                    List<String> images = getBikeImages(bike.getId());
                    return BikeMapper.toDTO(bike, brandName, categoryName, modelName, status, images);
                });
    }




    @Override
    public BikeResponseDTO getBikeById(int id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found with id " + id));
        String brandName = brandService.getById(bike.getBrandId()).getBrandName();
        String categoryName = categoryService.getCategoryById(bike.getCategoryId()).getCategoryName();
        String modelName = modelService.getById(bike.getModelId()).getModelName();
        String status = bike.getBikeStatus() != null ? bike.getBikeStatus().name() : "UNKNOWN";
        List<String> images = getBikeImages(id);
        return BikeMapper.toDTO(bike, brandName, categoryName, modelName, status, images);
    }



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
        List<Integer> activeStatuses = List.of(1, 2, 7);

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

    @Override
    public List<BikeResponseDTO> searchBikes(String searchText) {
        List<Bike> bikes;
        if (searchText == null || searchText.trim().isEmpty()) {
            bikes = bikeRepository.findAll();
        } else {
            bikes = bikeRepository.searchBikesByText(searchText.trim());
        }
        return bikes.stream()
                .map(bike -> {
                    String brandName = brandService.getById(bike.getBrandId()).getBrandName();
                    String categoryName = categoryService.getCategoryById(bike.getCategoryId()).getCategoryName();
                    String modelName = modelService.getById(bike.getModelId()).getModelName();
                    String status = bike.getBikeStatus() != null ? bike.getBikeStatus().name() : "UNKNOWN";
                    List<String> images = getBikeImages(bike.getId());
                    return BikeMapper.toDTO(bike, brandName, categoryName, modelName, status, images);
                })
                .toList();
    }

}
