package ru.rustam.catalog.mapper;

import org.springframework.stereotype.Component;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.FileDto;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.entity.FileEntity;

import java.util.List;

@Component
public class CatalogMapper {
    private final FileMapper fileMapper;

    public CatalogMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public CatalogEntity toEntity(CreateCatalogDto dto) {
        CatalogEntity e = new CatalogEntity();
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setPrice(dto.getPrice());
        e.setPrimaryImage(dto.getPrimaryImage());
        return e;
    }
    public CatalogDto toDto(CatalogEntity e) {
        CatalogDto dto = new CatalogDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setPrice(e.getPrice());
        List<FileDto> images = (e.getImages() == null ? List.<FileEntity>of() : e.getImages())
                .stream()
                .map(fileMapper::toDto)
                .toList();
        dto.setImages(images);
        dto.setPrimaryImage(e.getPrimaryImage());
        return dto;
    }
}
