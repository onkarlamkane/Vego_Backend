package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.VehicleTypeDto;
import com.eptiq.vegobike.exceptions.DuplicateResourceException;
import com.eptiq.vegobike.model.VehicleType;
import com.eptiq.vegobike.repositories.VehicleTypeRepository;
import com.eptiq.vegobike.services.VehicleTypeService;
import com.eptiq.vegobike.mappers.VehicleTypeMapper;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleTypeServiceImpl implements VehicleTypeService {

    private final VehicleTypeRepository repository;
    private final VehicleTypeMapper mapper;

    @Override
    public List<VehicleTypeDto> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public List<VehicleTypeDto> getActive() {
        return mapper.toDtoList(repository.findByIsActive(1));
    }

    @Override
    public VehicleTypeDto getById(Integer id) {
        VehicleType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public VehicleTypeDto create(VehicleTypeDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty())
            throw new IllegalArgumentException("Name is required");

        if (repository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new DuplicateResourceException("Type '" + dto.getName() + "' already exists");

        try {
            VehicleType entity = new VehicleType();
            entity.setName(dto.getName().trim());
            entity.setIsActive(1);
            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            VehicleType saved = repository.save(entity);
            log.info("VehicleType created: {}", saved.getId());
            return mapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Database error while creating vehicle type", e);
        }
    }

    @Override
    @Transactional
    public VehicleTypeDto update(Integer id, VehicleTypeDto dto) {
        VehicleType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found: " + id));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            if (repository.existsByNameIgnoreCaseAndIdNot(dto.getName().trim(), id))
                throw new IllegalArgumentException("Type '" + dto.getName() + "' already exists");
            entity.setName(dto.getName().trim());
        }
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        VehicleType saved = repository.save(entity);
        log.info("VehicleType updated: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public VehicleTypeDto toggleStatus(Integer id) {
        VehicleType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found: " + id));
        entity.setIsActive(entity.getIsActive() != null && entity.getIsActive() == 1 ? 0 : 1);
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        VehicleType saved = repository.save(entity);
        log.info("VehicleType status toggled: id={} now isActive={}", saved.getId(), saved.getIsActive());
        return mapper.toDto(saved);
    }


}
