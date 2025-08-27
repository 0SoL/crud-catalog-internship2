package ru.rustam.catalog.service;

import org.springframework.stereotype.Service;
import ru.rustam.catalog.dto.CategoryDto;
import ru.rustam.catalog.entity.CategoryEntity;
import ru.rustam.catalog.exception.FileException;
import ru.rustam.catalog.mapper.CategoryMapper;
import ru.rustam.catalog.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryDto> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryDto create(CategoryDto categoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(categoryDto)));
    }

    public CategoryDto update(Integer id, CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new FileException("Error"));
        categoryEntity.setName(categoryDto.getName());
        categoryRepository.save(categoryEntity);
        return categoryMapper.toDto(categoryEntity);
    }

    public void delete(Integer id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new FileException("Error"));
        categoryRepository.delete(categoryEntity);
    }
}
