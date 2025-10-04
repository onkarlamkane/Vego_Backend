package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.model.Category;
import com.eptiq.vegobike.dtos.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity → DTO
    CategoryDTO toDTO(Category category);

    // DTO → Entity
    Category toEntity(CategoryDTO dto);

    // List<Entity> → List<DTO>
    List<CategoryDTO> toDTOList(List<Category> categories);

    // Update existing entity from DTO (for edit/update flow)
    void updateEntityFromDTO(CategoryDTO dto, @MappingTarget Category category);
}
