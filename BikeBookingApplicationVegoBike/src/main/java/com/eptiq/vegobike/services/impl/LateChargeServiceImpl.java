package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.LateChargeRequestDTO;
import com.eptiq.vegobike.dtos.LateChargeResponseDTO;
import com.eptiq.vegobike.exceptions.DuplicateResourceException;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.LateChargeMapper;
import com.eptiq.vegobike.model.LateCharge;
import com.eptiq.vegobike.repositories.LateChargeRepository;
import com.eptiq.vegobike.services.LateChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LateChargeServiceImpl implements LateChargeService {

    private final LateChargeRepository repository;
    private final LateChargeMapper mapper;


    @Override
    public LateChargeResponseDTO createLateCharge(LateChargeRequestDTO request) {
        boolean exists = repository.existsByCategoryIdAndChargeType(request.getCategoryId(), request.getChargeType());
        if (exists) {
            throw new DuplicateResourceException("Late charge with this category and charge type already exists");
        }
        LateCharge entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }



    @Override
    public LateChargeResponseDTO updateLateCharge(Integer id, LateChargeRequestDTO request) {
        LateCharge entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LateCharge not found with id: " + id));
        entity.setCategoryId(request.getCategoryId());
        entity.setChargeType(request.getChargeType());
        entity.setCharge(request.getCharge());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public LateChargeResponseDTO getLateChargeById(Integer id) {
        LateCharge entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LateCharge not found with id: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    public List<LateChargeResponseDTO> getAllLateCharges() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteLateCharge(Integer id) {
        LateCharge entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LateCharge not found with id: " + id));
        repository.delete(entity);
    }

    @Override
    public List<LateChargeResponseDTO> getAllActiveLateCharges() {
        return repository.findAllByIsActive(1)
                .stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LateChargeResponseDTO changeStatus(Integer id, Integer isActive) {
        LateCharge entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LateCharge not found with id: " + id));
        entity.setIsActive(isActive);
        return mapper.toResponse(repository.save(entity));
    }



}
