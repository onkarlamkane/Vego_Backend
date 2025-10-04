package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.BikeSaleEnquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BikeSaleEnquiryService {

    Page<BikeSaleEnquiryDTO> getAllEnquiries(Pageable pageable);
    BikeSaleEnquiryDTO getEnquiryById(Long id);
    BikeSaleEnquiryDTO saveEnquiry(BikeSaleEnquiryDTO dto);
    BikeSaleEnquiryDTO updateEnquiry(Long id, BikeSaleEnquiryDTO dto);
    void deleteEnquiry(Long id);
    BikeSaleEnquiryDTO updateEnquiryStatus(Long id, String status);
    List<BikeSaleEnquiryDTO> getAllEnquiries();
}