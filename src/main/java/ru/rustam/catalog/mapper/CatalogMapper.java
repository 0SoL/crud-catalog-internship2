package ru.rustam.catalog.mapper;

import org.springframework.stereotype.Component;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.entity.CatalogEntity;

@Component
public class CatalogMapper {
    public CatalogEntity toEntity(CreateCatalogDto dto) {
        CatalogEntity e = new CatalogEntity();
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setPrice(dto.getPrice());
        return e;
    }
    public CatalogDto toDto(CatalogEntity e) {
        CatalogDto dto = new CatalogDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setPrice(e.getPrice());
        return dto;
    }
}
