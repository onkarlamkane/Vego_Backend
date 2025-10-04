package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BikeSaleEnquiryDTO;
import com.eptiq.vegobike.mappers.BikeSaleEnquiryMapper;
import com.eptiq.vegobike.model.BikeSaleEnquiry;
import com.eptiq.vegobike.repositories.BikeSaleEnquiryRepository;
import com.eptiq.vegobike.services.BikeSaleEnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BikeSaleEnquiryServiceImpl implements BikeSaleEnquiryService {
    private final BikeSaleEnquiryRepository repository;
    private final BikeSaleEnquiryMapper mapper;

    @Override
    public Page<BikeSaleEnquiryDTO> getAllEnquiries(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    public BikeSaleEnquiryDTO getEnquiryById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));
    }

    @Override
    public BikeSaleEnquiryDTO saveEnquiry(BikeSaleEnquiryDTO dto) {
        BikeSaleEnquiry entity = mapper.toEntity(dto);
        // Set current timestamp directly
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return mapper.toDTO(repository.save(entity));
    }


    @Override
    public BikeSaleEnquiryDTO updateEnquiry(Long id, BikeSaleEnquiryDTO dto) {
        BikeSaleEnquiry existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));

        // Update fields with correct mapping
        existing.setStatus(dto.getStatus());
        existing.setCustomerId(dto.getCustomerId());
        existing.setBikeId(dto.getBikeId());
        existing.setEnquiryId(dto.getEnquiryId());
        existing.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Update timestamp

        // Save and return updated DTO
        return mapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteEnquiry(Long id) {
        repository.deleteById(id);
    }

    @Override
    public BikeSaleEnquiryDTO updateEnquiryStatus(Long id, String status) {
        BikeSaleEnquiry enquiry = repository.findById(id).orElseThrow(() -> new RuntimeException("Enquiry not found"));
        enquiry.setStatus(status);
        BikeSaleEnquiry updatedEnquiry = repository.save(enquiry);
        return mapper.toDTO(updatedEnquiry);
    }

    @Override
    public List<BikeSaleEnquiryDTO> getAllEnquiries() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

}