//package com.eptiq.vegobike.services.impl;
//
//import com.eptiq.vegobike.dtos.BrandDTO;
//import com.eptiq.vegobike.mappers.BrandMapper;
//import com.eptiq.vegobike.model.Brand;
//import com.eptiq.vegobike.repositories.BrandRepository;
//import com.eptiq.vegobike.services.BrandService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import jakarta.persistence.EntityNotFoundException;
//import java.io.IOException;
//import java.nio.file.*;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Slf4j
//@Service
//public class BrandServiceImpl implements BrandService {
//
//    private final BrandRepository brandRepository;
//    private final BrandMapper brandMapper;
//
//    @Value("${app.upload.dir:uploads/brands}")
//    private String uploadDir;
//
//    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
//        this.brandRepository = brandRepository;
//        this.brandMapper = brandMapper;
//    }
//
//    @Override
//    @Transactional
//    public BrandDTO create(BrandDTO dto, MultipartFile image) throws IOException {
//        if (dto.getBrandName() == null || dto.getBrandName().trim().isEmpty()) {
//            throw new IllegalArgumentException("Brand name is required");
//        }
//
//        // ✅ Prevent duplicate brand name (instead of relying on DB exception)
//        if (brandRepository.existsByBrandNameIgnoreCase(dto.getBrandName().trim())) {
//            throw new IllegalArgumentException("Brand with name '" + dto.getBrandName() + "' already exists");
//        }
//
//        try {
//            Brand entity = new Brand();
//            entity.setBrandName(dto.getBrandName().trim());
//            entity.setCategoryId(dto.getCategoryId());
//            entity.setIsActive(1);
//            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//            entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//            if (image != null && !image.isEmpty()) {
//                String filename = storeImage(image);
//                entity.setBrandImage(filename);
//            }
//
//            Brand saved = brandRepository.save(entity);
//            return brandMapper.toDTO(saved);
//
//        } catch (DataIntegrityViolationException e) {
//            throw new IllegalArgumentException("Database error while creating brand", e);
//        }
//    }
//
//    @Override
//    @Transactional
//    public BrandDTO update(Integer id, BrandDTO dto, MultipartFile image) throws IOException {
//        Brand entity = brandRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
//
//        if (dto.getBrandName() != null && !dto.getBrandName().trim().isEmpty()) {
//            if (!dto.getBrandName().equalsIgnoreCase(entity.getBrandName())
//                    && brandRepository.existsByBrandNameIgnoreCase(dto.getBrandName())) {
//                throw new IllegalArgumentException("Brand with name '" + dto.getBrandName() + "' already exists");
//            }
//            entity.setBrandName(dto.getBrandName().trim());
//        }
//
//        if (dto.getCategoryId() != null) {
//            entity.setCategoryId(dto.getCategoryId());
//        }
//
//        if (image != null && !image.isEmpty()) {
//            String filename = storeImage(image);
//            if (entity.getBrandImage() != null) deleteOld(entity.getBrandImage());
//            entity.setBrandImage(filename);
//        }
//
//        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        Brand saved = brandRepository.save(entity);
//        return brandMapper.toDTO(saved);
//    }
//
//
//    @Override
//    @Transactional
//    public void toggleStatus(Integer id) {
//        Brand entity = Optional.ofNullable(brandRepository.findAnyById(id))
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
//        entity.setIsActive(entity.getIsActive() != null && entity.getIsActive() == 1 ? 0 : 1);
//        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        brandRepository.save(entity);
//    }
//
//    @Override
//    @Transactional
//    public void delete(Integer id) {
//        Brand entity = brandRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
//        if (entity.getBrandImage() != null) deleteOld(entity.getBrandImage());
//        brandRepository.deleteById(id);
//    }
//
//    @Override
//    public BrandDTO getById(Integer id) {
//        Brand entity = brandRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
//        return brandMapper.toDTO(entity);
//    }
//
//    @Override
//    public Page<BrandDTO> list(Pageable pageable) {
//        return brandRepository.findAll(pageable).map(brandMapper::toDTO);
//    }
//
//    @Override
//    public List<BrandDTO> listActive() {
//        return brandMapper.toDTOList(brandRepository.findByIsActive(1, Pageable.unpaged()).getContent());
//    }
//
//    @Override
//    public boolean existsByName(String brandName) {
//        return brandRepository.existsByBrandNameIgnoreCase(brandName);
//    }
//
//    private String storeImage(MultipartFile image) throws IOException {
//        Path uploadPath = Paths.get(uploadDir);
//        Files.createDirectories(uploadPath);
//        String ext = "";
//        String original = image.getOriginalFilename();
//        if (original != null && original.contains(".")) {
//            ext = original.substring(original.lastIndexOf("."));
//        }
//        String filename = UUID.randomUUID() + ext;
//        Path target = uploadPath.resolve(filename);
//        Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//        log.info("Stored brand image: {}", target);
//        return filename;
//    }
//
//    private void deleteOld(String fileName) {
//        try {
//            Path p = Paths.get(uploadDir).resolve(fileName);
//            if (Files.exists(p)) Files.delete(p);
//        } catch (IOException e) {
//            log.warn("Failed to delete old image {}: {}", fileName, e.getMessage());
//        }
//    }
//}




package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BrandDTO;
import com.eptiq.vegobike.mappers.BrandMapper;
import com.eptiq.vegobike.model.Brand;
import com.eptiq.vegobike.repositories.BrandRepository;
import com.eptiq.vegobike.services.BrandService;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ImageUtils imageUtils; // ✅ Injected

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper, ImageUtils imageUtils) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.imageUtils = imageUtils;
    }

    @Override
    @Transactional
    public BrandDTO create(BrandDTO dto, MultipartFile image) throws IOException {
        if (dto.getBrandName() == null || dto.getBrandName().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name is required");
        }

        // ✅ Prevent duplicate brand name
        if (brandRepository.existsByBrandNameIgnoreCase(dto.getBrandName().trim())) {
            throw new IllegalArgumentException("Brand with name '" + dto.getBrandName() + "' already exists");
        }

        try {
            Brand entity = new Brand();
            entity.setBrandName(dto.getBrandName().trim());
            entity.setCategoryId(dto.getCategoryId());
            entity.setIsActive(1);
            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            if (image != null && !image.isEmpty()) {
                String relativePath = imageUtils.storeBrandImage(image); // ✅ Use ImageUtils
                entity.setBrandImage(relativePath);
            }

            Brand saved = brandRepository.save(entity);
            return brandMapper.toDTO(saved);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Database error while creating brand", e);
        }
    }

    @Override
    @Transactional
    public BrandDTO update(Integer id, BrandDTO dto, MultipartFile image) throws IOException {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));

        if (dto.getBrandName() != null && !dto.getBrandName().trim().isEmpty()) {
            if (!dto.getBrandName().equalsIgnoreCase(entity.getBrandName())
                    && brandRepository.existsByBrandNameIgnoreCase(dto.getBrandName())) {
                throw new IllegalArgumentException("Brand with name '" + dto.getBrandName() + "' already exists");
            }
            entity.setBrandName(dto.getBrandName().trim());
        }

        if (dto.getCategoryId() != null) {
            entity.setCategoryId(dto.getCategoryId());
        }

        if (image != null && !image.isEmpty()) {
            // ✅ Delete old image if exists
            if (entity.getBrandImage() != null) {
                imageUtils.deleteImage(entity.getBrandImage());
            }

            // ✅ Save new image
            String relativePath = imageUtils.storeBrandImage(image);
            entity.setBrandImage(relativePath);
        }

        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        Brand saved = brandRepository.save(entity);
        return brandMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void toggleStatus(Integer id) {
        Brand entity = Optional.ofNullable(brandRepository.findAnyById(id))
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
        entity.setIsActive(entity.getIsActive() != null && entity.getIsActive() == 1 ? 0 : 1);
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        brandRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
        if (entity.getBrandImage() != null) {
            imageUtils.deleteImage(entity.getBrandImage()); // ✅ Use ImageUtils
        }
        brandRepository.deleteById(id);
    }

    @Override
    public BrandDTO getById(Integer id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + id));
        return brandMapper.toDTO(entity);
    }

    @Override
    public Page<BrandDTO> list(Pageable pageable) {
        return brandRepository.findAll(pageable).map(brandMapper::toDTO);
    }

    @Override
    public List<BrandDTO> listActive() {
        return brandMapper.toDTOList(brandRepository.findByIsActive(1, Pageable.unpaged()).getContent());
    }

    @Override
    public boolean existsByName(String brandName) {
        return brandRepository.existsByBrandNameIgnoreCase(brandName);
    }
}
