package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BikeServiceEnquiryDto;
import com.eptiq.vegobike.mappers.BikeServiceEnquiryMapper;
import com.eptiq.vegobike.model.BikeServiceEnquiry;
import com.eptiq.vegobike.repositories.BikeServiceEnquiryRepository;
import com.eptiq.vegobike.services.BikeServiceEnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BikeServiceEnquiryServiceImpl implements BikeServiceEnquiryService {

    private final BikeServiceEnquiryRepository repository;
    private final BikeServiceEnquiryMapper mapper;

    @Override
    public BikeServiceEnquiryDto addToCart(BikeServiceEnquiryDto dto) {
        BikeServiceEnquiry entity = mapper.toEntity(dto);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        BikeServiceEnquiry saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<BikeServiceEnquiryDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)   // ✅ use instance 'mapper', not class name
                .toList();
    }


    @Override
    public List<BikeServiceEnquiryDto> getCartByCustomer(Long customerId) {
        return repository.findAll()
                .stream()
                .filter(e -> e.getCustomerId() == customerId)
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromCart(Long id) {
        repository.deleteById(id);
    }
}
