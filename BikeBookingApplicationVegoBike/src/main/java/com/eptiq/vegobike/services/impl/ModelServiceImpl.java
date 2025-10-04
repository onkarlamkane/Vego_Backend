//
//package com.eptiq.vegobike.services.impl;
//
//import com.eptiq.vegobike.dtos.ModelCreateRequest;
//import com.eptiq.vegobike.dtos.ModelResponse;
//import com.eptiq.vegobike.dtos.ModelUpdateRequest;
//import com.eptiq.vegobike.mappers.ModelMapper;
//import com.eptiq.vegobike.model.Brand;
//import com.eptiq.vegobike.model.Model;
//import com.eptiq.vegobike.repositories.BrandRepository;
//import com.eptiq.vegobike.repositories.ModelRepository;
//import com.eptiq.vegobike.services.ModelService;
//import com.eptiq.vegobike.utils.ImageUtils;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ModelServiceImpl implements ModelService {
//
//    private final ModelRepository modelRepository;
//    private final BrandRepository brandRepository;
//    private final ModelMapper mapper;
//    private final ImageUtils imageUtils;
//
//    @Override
//    public ModelResponse create(ModelCreateRequest request, MultipartFile image) {
//        Brand brand = brandRepository.findById(request.getBrandId())
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + request.getBrandId()));
//
//        Model model = new Model();
//        model.setBrandId(request.getBrandId());   // ensure brandId is set
//        model.setBrand(brand);
//        model.setModelName(request.getModelName().trim());
//        model.setIsActive(1);
//
//        if (image != null && !image.isEmpty()) {
//            try {
//                String storedPath = imageUtils.storeModelImage(image);
//                model.setModelImage(storedPath);
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to store model image", e);
//            }
//        }
//
//        return mapper.toResponse(modelRepository.save(model));
//    }
//
//
//    @Override
//    public ModelResponse update(Integer id, ModelUpdateRequest request, MultipartFile image) {
//        Model model = modelRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));
//
//        if (request.getBrandId() != null) {
//            Brand brand = brandRepository.findById(request.getBrandId())
//                    .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + request.getBrandId()));
//            model.setBrand(brand);
//        }
//
//        if (request.getModelName() != null && !request.getModelName().trim().isEmpty()) {
//            model.setModelName(request.getModelName().trim());
//        }
//
//        if (image != null && !image.isEmpty()) {
//            try {
//                String storedPath = imageUtils.storeModelImage(image);
//                model.setModelImage(storedPath);
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to update model image", e);
//            }
//        }
//
//        return mapper.toResponse(modelRepository.save(model));
//    }
//
//    @Override
//    public void delete(Integer id) {
//        if (!modelRepository.existsById(id)) {
//            throw new EntityNotFoundException("Model not found with ID: " + id);
//        }
//        modelRepository.deleteById(id);
//    }
//
//    @Override
//    public ModelResponse getById(Integer id) {
//        return modelRepository.findById(id)
//                .map(mapper::toResponse)
//                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));
//    }
//
//    @Override
//    public List<ModelResponse> listAll() {
//        return modelRepository.findAll().stream().map(mapper::toResponse).toList();
//    }
//
//    @Override
//    public Page<ModelResponse> list(Pageable pageable) {
//        return modelRepository.findAll(pageable).map(mapper::toResponse);
//    }
//
//    @Override
//    public Page<ModelResponse> searchByName(String query, Pageable pageable) {
//        if (query == null || query.trim().isEmpty()) {
//            return modelRepository.findAll(pageable).map(mapper::toResponse);
//        }
//        return modelRepository.findByModelNameContainingIgnoreCase(query.trim(), pageable)
//                .map(mapper::toResponse);
//    }
//
//    @Override
//    public List<ModelResponse> listActive() {
//        return modelRepository.findByIsActiveEquals(1).stream().map(mapper::toResponse).toList();
//    }
//
//    @Override
//    public ModelResponse toggleStatus(Integer id) {
//        Model model = modelRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));
//        model.setIsActive(model.getIsActive() == 1 ? 0 : 1);
//        return mapper.toResponse(modelRepository.save(model));
//    }
//
//    @Override
//    public List<ModelResponse> getModelsByBrand(Integer brandId) {
//        List<Model> models = modelRepository.findByBrandIdAndIsActiveTrue(brandId);
//        return models.stream()
//                .map(mapper::toResponse)
//                .collect(Collectors.toList());
//    }
//
//}

package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.ModelCreateRequest;
import com.eptiq.vegobike.dtos.ModelResponse;
import com.eptiq.vegobike.dtos.ModelUpdateRequest;
import com.eptiq.vegobike.mappers.ModelMapper;
import com.eptiq.vegobike.model.Brand;
import com.eptiq.vegobike.model.Model;
import com.eptiq.vegobike.repositories.BrandRepository;
import com.eptiq.vegobike.repositories.ModelRepository;
import com.eptiq.vegobike.services.ModelService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper mapper;
    private final ImageUtils imageUtils;

    @Override
    public ModelResponse create(ModelCreateRequest request, MultipartFile image) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + request.getBrandId()));

        Model model = new Model();
        model.setBrandId(request.getBrandId());
        model.setBrand(brand);
        model.setModelName(request.getModelName().trim());
        model.setIsActive(1);

        if (image != null && !image.isEmpty()) {
            try {
                String storedPath = imageUtils.storeModelImage(image);
                model.setModelImage(storedPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store model image", e);
            }
        }

        return mapper.toResponse(modelRepository.save(model));
    }

    @Override
    public ModelResponse update(Integer id, ModelUpdateRequest request, MultipartFile image) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + request.getBrandId()));
            model.setBrand(brand);
        }

        if (request.getModelName() != null && !request.getModelName().trim().isEmpty()) {
            model.setModelName(request.getModelName().trim());
        }

        if (image != null && !image.isEmpty()) {
            try {
                String storedPath = imageUtils.storeModelImage(image);
                model.setModelImage(storedPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update model image", e);
            }
        }

        return mapper.toResponse(modelRepository.save(model));
    }

    @Override
    public void delete(Integer id) {
        if (!modelRepository.existsById(id)) {
            throw new EntityNotFoundException("Model not found with ID: " + id);
        }
        modelRepository.deleteById(id);
    }

    @Override
    public ModelResponse getById(Integer id) {
        return modelRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));
    }

    @Override
    public List<ModelResponse> listAll() {
        return modelRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public Page<ModelResponse> list(Pageable pageable) {
        return modelRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public Page<ModelResponse> searchByName(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return modelRepository.findAll(pageable).map(mapper::toResponse);
        }
        return modelRepository.findByModelNameContainingIgnoreCase(query.trim(), pageable)
                .map(mapper::toResponse);
    }

    @Override
    public List<ModelResponse> listActive() {
        return modelRepository.findByIsActiveEquals(1)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ModelResponse toggleStatus(Integer id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with ID: " + id));
        model.setIsActive(model.getIsActive() == 1 ? 0 : 1);
        return mapper.toResponse(modelRepository.save(model));
    }

    @Override
    public List<ModelResponse> getModelsByBrand(Integer brandId) {
        List<Model> models = modelRepository.findByBrandIdAndIsActiveEquals(brandId, 1);
        return models.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}