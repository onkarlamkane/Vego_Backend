package com.eptiq.vegobike.services;
import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.dtos.BikeSellRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface BikeSaleService {
    //BikeSaleDTO getBikeSaleById(Long id);
    BikeSellImageDTO updateBikeImages(Long bikeId, BikeSellImageDTO bikeSellImageDTO);
    List<BikeSaleDTO> getAllBikeSales();
    void sellBike(BikeSellRequestDTO requestDTO) throws IOException;
    void deleteBikeSale(Long id);// ✅ delete bike
    void deleteSpecificBikeImage(Long bikeId, String imageType) throws Exception;
    List<BikeSaleDTO> getAllListedBikeSales();
    Map<String, Object> getBikeSaleById(Long id);
    List<Map<String, Object>> getAllListedBikeSalesWithImages();
    BikeSaleDTO updateBikeSaleAdmin(
            Long id,
            BikeSaleDTO bikeSaleDTO,
            MultipartFile pucImage,
            MultipartFile documentImage
    ) throws IOException;
}

