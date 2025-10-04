package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.CityDto;
import com.eptiq.vegobike.model.City;
import com.eptiq.vegobike.repositories.CityRepository;
import com.eptiq.vegobike.services.CityService;
import com.eptiq.vegobike.mappers.CityMapper;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final ImageUtils imageUtils;

    @Override
    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream().map(cityMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CityDto getCityById(Integer id) {
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        return cityMapper.toDto(city);
    }



    @Override
    @Transactional
    public CityDto createCity(CityDto dto, MultipartFile image) throws IOException {
        if (dto.getCityName() == null || dto.getCityName().trim().isEmpty()) {
            throw new IllegalArgumentException("City name is required");
        }

        // Prevent duplicate city name (case-insensitive)
        if (cityRepository.existsByCityNameIgnoreCase(dto.getCityName().trim())) {
            throw new IllegalArgumentException("City with name '" + dto.getCityName() + "' already exists");
        }

        try {
            City entity = new City();
            entity.setCityName(dto.getCityName().trim());
            entity.setIsActive(1);
            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            if (image != null && !image.isEmpty()) {
                String relativePath = imageUtils.storeCityImage(image);
                entity.setCityImage(relativePath);
            }

            City saved = cityRepository.save(entity);
            return cityMapper.toDto(saved);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Database error while creating city", e);
        }
    }

    @Override
    @Transactional
    public CityDto updateCity(Integer id, CityDto dto, MultipartFile image) throws IOException {
        City entity = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));

        if (dto.getCityName() != null && !dto.getCityName().trim().isEmpty()) {
            // Optional: check duplicate on update except current record
            if (cityRepository.existsByCityNameIgnoreCaseAndIdNot(dto.getCityName().trim(), id)) {
                throw new IllegalArgumentException("City with name '" + dto.getCityName() + "' already exists");
            }
            entity.setCityName(dto.getCityName().trim());
        }

        if (image != null && !image.isEmpty()) {
            String relativePath = imageUtils.storeCityImage(image);
            entity.setCityImage(relativePath);
        }

        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        City saved = cityRepository.save(entity);
        return cityMapper.toDto(saved);
    }

    @Override
    public List<CityDto> getActiveCities() {
        return cityRepository.findByIsActive(1).stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CityDto toggleCityStatus(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));

        // Toggle the isActive flag: 1 -> 0 or 0 -> 1
        city.setIsActive(city.getIsActive() != null && city.getIsActive() == 1 ? 0 : 1);
        city.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        City saved = cityRepository.save(city);
        return cityMapper.toDto(saved);
    }



}