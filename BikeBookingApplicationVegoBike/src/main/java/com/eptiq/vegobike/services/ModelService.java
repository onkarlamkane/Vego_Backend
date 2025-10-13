package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.ModelCreateRequest;
import com.eptiq.vegobike.dtos.ModelResponse;
import com.eptiq.vegobike.dtos.ModelUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModelService {

    ModelResponse create(ModelCreateRequest request, MultipartFile image);
    ModelResponse update(Integer id, ModelUpdateRequest request, MultipartFile image);

    void delete(Integer id);
    ModelResponse getById(Integer id);

    List<ModelResponse> listAll();
    Page<ModelResponse> list(Pageable pageable);

    Page<ModelResponse> searchByName(String query, Pageable pageable);

    ModelResponse toggleStatus(Integer id);

    List<ModelResponse> listActive();
    List<ModelResponse> getModelsByBrand(Integer brandId);

}
