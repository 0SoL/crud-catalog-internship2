package ru.rustam.catalog.catalog.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.rustam.catalog.catalog.dto.CatalogDto;
import ru.rustam.catalog.catalog.entity.CatalogEntity;

public interface CatalogMapping {
    public static CatalogEntity toEntity(CatalogDto dto) {
        CatalogEntity e = new CatalogEntity();
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setPrice(dto.getPrice());
        return e;
    }
    public static CatalogDto toDto(CatalogEntity e) {
        CatalogDto dto = new CatalogDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setPrice(e.getPrice());
        return dto;
    }
}
