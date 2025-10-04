package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile image) throws IOException;

    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO, MultipartFile image) throws IOException;

    void toggleCategoryStatus(Integer id);

    Page<CategoryDTO> getAllCategories(Pageable pageable);

    Page<CategoryDTO> getCategoriesByStatus(Integer status, Pageable pageable);

    CategoryDTO getCategoryById(Integer id);

    List<CategoryDTO> getActiveCategories();

    Page<CategoryDTO> searchCategoriesByName(String name, Pageable pageable);

    boolean categoryExistsByName(String categoryName);

    long getCategoryCount();

    long getActiveCategoryCount();
}
