//package com.eptiq.vegobike.services.impl;
//
//import com.eptiq.vegobike.dtos.CategoryDTO;
//import com.eptiq.vegobike.exceptions.CategoryNotFoundException;
//import com.eptiq.vegobike.exceptions.DuplicateCategoryException;
//import com.eptiq.vegobike.exceptions.FileStorageException;
//import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
//import com.eptiq.vegobike.mappers.CategoryMapper;
//import com.eptiq.vegobike.model.Category;
//import com.eptiq.vegobike.model.VehicleType;
//import com.eptiq.vegobike.repositories.CategoryRepository;
//import com.eptiq.vegobike.repositories.VehicleTypeRepository;
//import com.eptiq.vegobike.services.CategoryService;
//import com.eptiq.vegobike.utils.ImageUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Slf4j
//@Service
//public class CategoryServiceImpl implements CategoryService {
//
//    private final CategoryRepository categoryRepository;
//    private final CategoryMapper categoryMapper;
//    private final VehicleTypeRepository vehicleTypeRepository;
//    private final ImageUtils imageUtils; // ✅ central utility
//
//    public CategoryServiceImpl(CategoryRepository categoryRepository,
//                               CategoryMapper categoryMapper,
//                               ImageUtils imageUtils , VehicleTypeRepository vehicleTypeRepository) {
//        this.categoryRepository = categoryRepository;
//        this.categoryMapper = categoryMapper;
//        this.imageUtils = imageUtils;
//        this.vehicleTypeRepository = vehicleTypeRepository;
//    }
//
//    @Override
//    @Transactional
//    public CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile image) throws IOException {
//        if (categoryExistsByName(categoryDTO.getCategoryName())) {
//            throw new DuplicateCategoryException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
//        }
//        if (categoryDTO.getVehicleTypeId() == null ||
//                !vehicleTypeRepository.existsById(categoryDTO.getVehicleTypeId())) {
//            throw new IllegalArgumentException("Valid VehicleType is required before adding a Category");
//        }
//
//        VehicleType vehicleType = vehicleTypeRepository.findById(categoryDTO.getVehicleTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("VehicleType not found"));
//
//        // Use mapper to create entity from DTO
//        Category category = categoryMapper.toEntity(categoryDTO);
//
//        // Set the VehicleType explicitly (to ensure proper entity reference)
//        category.setVehicleType(vehicleType);
//
//        // Set audit and status fields if not set by mapper
//        category.setIsActive(1);
//        category.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//        if (image != null && !image.isEmpty()) {
//            String relativePath = imageUtils.storeCategoryImage(image);
//            category.setImage(relativePath);
//        }
//
//        Category saved = categoryRepository.save(category);
//        return categoryMapper.toDTO(saved);
//    }
//
//    @Override
//    @Transactional
//    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO, MultipartFile image) throws IOException {
//        Category existing = categoryRepository.findById(id)
//                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
//
//        if (!existing.getCategoryName().equalsIgnoreCase(categoryDTO.getCategoryName())
//                && categoryExistsByName(categoryDTO.getCategoryName())) {
//            throw new DuplicateCategoryException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
//        }
//
//        existing.setCategoryName(categoryDTO.getCategoryName());
//        existing.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//        // Validate and update VehicleType if changed
//        if (categoryDTO.getVehicleTypeId() != null &&
//                (existing.getVehicleType() == null ||
//                        !existing.getVehicleType().getId().equals(categoryDTO.getVehicleTypeId()))) {
//            VehicleType vehicleType = vehicleTypeRepository.findById(categoryDTO.getVehicleTypeId())
//                    .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found"));
//            existing.setVehicleType(vehicleType);
//        }
//
//        if (image != null && !image.isEmpty()) {
//            if (existing.getImage() != null) {
//                imageUtils.deleteImage(existing.getImage());
//            }
//            String relativePath = imageUtils.storeCategoryImage(image);
//            existing.setImage(relativePath);
//        }
//
//        Category saved = categoryRepository.save(existing);
//        return categoryMapper.toDTO(saved);
//    }
//
//
//    @Override
//    @Transactional
//    public void toggleCategoryStatus(Integer id) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
//
//        category.setIsActive(category.getIsActive() != null && category.getIsActive() == 1 ? 0 : 1);
//        category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        categoryRepository.save(category);
//    }
//
//
//    @Override
//    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
//
//        Page<CategoryDTO> category = categoryRepository.findAll(pageable).map(categoryMapper::toDTO){
//            CategoryDTO response =  categoryMapper.toDTO(category);
//        }
//        return categoryRepository.findAll(pageable).map(categoryMapper::toDTO);
//    }
//
//    @Override
//    public Page<CategoryDTO> getCategoriesByStatus(Integer status, Pageable pageable) {
//        return categoryRepository.findByIsActive(status, pageable).map(categoryMapper::toDTO);
//    }
//
//    @Override
//    public CategoryDTO getCategoryById(Integer id) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
//        return categoryMapper.toDTO(category);
//    }
//
//    @Override
//    public List<CategoryDTO> getActiveCategories() {
//        return categoryMapper.toDTOList(categoryRepository.findAllActiveCategories());
//    }
//
//    @Override
//    public Page<CategoryDTO> searchCategoriesByName(String name, Pageable pageable) {
//        return categoryRepository.searchByName(name, pageable).map(categoryMapper::toDTO);
//    }
//
//    @Override
//    public boolean categoryExistsByName(String categoryName) {
//        return categoryRepository.existsByCategoryNameIgnoreCase(categoryName);
//    }
//
//    @Override
//    public long getCategoryCount() {
//        return categoryRepository.count();
//    }
//
//    @Override
//    public long getActiveCategoryCount() {
//        return categoryRepository.countByStatus(1);
//    }
//
//    // utils
//    private String generateCorrelationId() {
//        return UUID.randomUUID().toString().substring(0, 8);
//    }
//
//    private String maskCategoryName(String categoryName) {
//        if (categoryName == null || categoryName.length() <= 4) return "";
//        return categoryName.substring(0, 2) + "" + categoryName.substring(categoryName.length() - 2);
//    }
//}

package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.CategoryDTO;
import com.eptiq.vegobike.exceptions.CategoryNotFoundException;
import com.eptiq.vegobike.exceptions.DuplicateCategoryException;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.CategoryMapper;
import com.eptiq.vegobike.model.Category;
import com.eptiq.vegobike.model.VehicleType;
import com.eptiq.vegobike.repositories.CategoryRepository;
import com.eptiq.vegobike.repositories.VehicleTypeRepository;
import com.eptiq.vegobike.services.CategoryService;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final ImageUtils imageUtils;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               ImageUtils imageUtils,
                               VehicleTypeRepository vehicleTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.imageUtils = imageUtils;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile image) throws IOException {
        log.info("CATEGORY_CREATE - name: {}", categoryDTO.getCategoryName());

        if (categoryExistsByName(categoryDTO.getCategoryName())) {
            throw new DuplicateCategoryException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
        }

        if (categoryDTO.getVehicleTypeId() == null ||
                !vehicleTypeRepository.existsById(categoryDTO.getVehicleTypeId())) {
            throw new IllegalArgumentException("Valid VehicleType is required before adding a Category");
        }

        VehicleType vehicleType = vehicleTypeRepository.findById(categoryDTO.getVehicleTypeId())
                .orElseThrow(() -> new IllegalArgumentException("VehicleType not found"));

        Category category = categoryMapper.toEntity(categoryDTO);
        category.setVehicleType(vehicleType);
        category.setIsActive(1);
        category.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if (image != null && !image.isEmpty()) {
            String relativePath = imageUtils.storeCategoryImage(image);
            category.setImage(relativePath);
            log.info("CATEGORY_CREATE_IMAGE_SAVED - {}", relativePath);
        }

        Category saved = categoryRepository.save(category);
        log.info("CATEGORY_CREATE_SUCCESS - id: {}", saved.getId());
        return categoryMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO, MultipartFile image) throws IOException {
        log.info("CATEGORY_UPDATE - id: {}", id);

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!existing.getCategoryName().equalsIgnoreCase(categoryDTO.getCategoryName())
                && categoryExistsByName(categoryDTO.getCategoryName())) {
            throw new DuplicateCategoryException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
        }

        existing.setCategoryName(categoryDTO.getCategoryName());
        existing.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if (categoryDTO.getVehicleTypeId() != null &&
                (existing.getVehicleType() == null ||
                        !existing.getVehicleType().getId().equals(categoryDTO.getVehicleTypeId()))) {
            VehicleType vehicleType = vehicleTypeRepository.findById(categoryDTO.getVehicleTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleType not found"));
            existing.setVehicleType(vehicleType);
        }

        if (image != null && !image.isEmpty()) {
            if (existing.getImage() != null) {
                imageUtils.deleteImage(existing.getImage());
            }
            String relativePath = imageUtils.storeCategoryImage(image);
            existing.setImage(relativePath);
            log.info("CATEGORY_UPDATE_IMAGE_SAVED - {}", relativePath);
        }

        Category saved = categoryRepository.save(existing);
        log.info("CATEGORY_UPDATE_SUCCESS - id: {}", saved.getId());
        return categoryMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void toggleCategoryStatus(Integer id) {
        log.info("CATEGORY_TOGGLE_STATUS - id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        category.setIsActive(category.getIsActive() != null && category.getIsActive() == 1 ? 0 : 1);
        category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        categoryRepository.save(category);

        log.info("CATEGORY_TOGGLE_STATUS_SUCCESS - id: {}, status: {}", id, category.getIsActive());
    }

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        log.info("CATEGORY_LIST_ALL - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return categoryRepository.findAll(pageable).map(categoryMapper::toDTO);
    }

    @Override
    public Page<CategoryDTO> getCategoriesByStatus(Integer status, Pageable pageable) {
        log.info("CATEGORY_LIST_BY_STATUS - status: {}", status);
        return categoryRepository.findByIsActive(status, pageable).map(categoryMapper::toDTO);
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        log.debug("CATEGORY_GET - id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    @Override
    public List<CategoryDTO> getActiveCategories() {
        log.info("CATEGORY_LIST_ACTIVE");
        return categoryMapper.toDTOList(categoryRepository.findAllActiveCategories());
    }

    @Override
    public Page<CategoryDTO> searchCategoriesByName(String name, Pageable pageable) {
        log.info("CATEGORY_SEARCH - name: {}", name);
        return categoryRepository.searchByName(name, pageable).map(categoryMapper::toDTO);
    }

    @Override
    public boolean categoryExistsByName(String categoryName) {
        return categoryRepository.existsByCategoryNameIgnoreCase(categoryName);
    }

    @Override
    public long getCategoryCount() {
        return categoryRepository.count();
    }

    @Override
    public long getActiveCategoryCount() {
        return categoryRepository.countByStatus(1);
    }

    // utils
    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String maskCategoryName(String categoryName) {
        if (categoryName == null || categoryName.length() <= 4) return "";
        return categoryName.substring(0, 2) + "" + categoryName.substring(categoryName.length() - 2);
    }
}