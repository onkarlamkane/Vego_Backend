package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.BrandDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BrandService {
    BrandDTO create(BrandDTO dto, MultipartFile image) throws IOException;
    BrandDTO update(Integer id, BrandDTO dto, MultipartFile image) throws IOException;
    void toggleStatus(Integer id);
    void delete(Integer id);
    BrandDTO getById(Integer id);
    Page<BrandDTO> list(Pageable pageable);
    List<BrandDTO> listActive();
    boolean existsByName(String brandName);
}