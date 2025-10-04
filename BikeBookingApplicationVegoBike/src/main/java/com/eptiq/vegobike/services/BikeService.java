package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.AvailableBikeDto;
import com.eptiq.vegobike.dtos.BikeDocumentsDTO;
import com.eptiq.vegobike.dtos.BikeRequestDTO;
import com.eptiq.vegobike.dtos.BikeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface BikeService {
    BikeResponseDTO createBike(BikeRequestDTO request) throws IOException;
    BikeResponseDTO updateBike(int id, BikeRequestDTO request) throws IOException;
    List<BikeResponseDTO> getAllBikes();
    BikeResponseDTO getBikeById(int id);
    Page<AvailableBikeDto> getAvailableBikes(Date startDate, Date endDate,
                                             String addressType, String search,
                                             Pageable pageable);

    BikeDocumentsDTO getBikeDocuments(int bikeId);
}
