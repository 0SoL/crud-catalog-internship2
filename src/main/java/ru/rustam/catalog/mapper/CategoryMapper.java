package ru.rustam.catalog.mapper;

import org.springframework.stereotype.Component;
import ru.rustam.catalog.dto.CategoryDto;
import ru.rustam.catalog.entity.CategoryEntity;

@Component
public class CategoryMapper {

    public CategoryDto toDto(CategoryEntity e) {
        CategoryDto dto = new CategoryDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        return dto;
    };

    public CategoryEntity toEntity(CategoryDto dto) {
        CategoryEntity e = new CategoryEntity();
        e.setId(dto.getId());
        e.setName(dto.getName());
        return e;
    }
}
