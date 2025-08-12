package ru.rustam.catalog.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.rustam.catalog.catalog.dto.CatalogDto;
import ru.rustam.catalog.catalog.dto.mapping.CatalogMapping;
import ru.rustam.catalog.catalog.entity.CatalogEntity;
import ru.rustam.catalog.catalog.repository.CatalogRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    // сервис для создания объекта
    public CatalogDto create(CatalogDto catalogDto) {
        CatalogEntity catalogEntity = catalogRepository.save(CatalogMapping.toEntity(catalogDto));
        return CatalogMapping.toDto(catalogEntity);
    }

    // сервис для поиска объекта по id
    public CatalogDto findById(Integer id) {
        CatalogEntity catalogEntity = catalogRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return CatalogMapping.toDto(catalogEntity);
    }

    // сервисч для вывода всех объектов
    public List<CatalogDto> findAll() {
        return catalogRepository.findAll().stream().map(CatalogMapping::toDto).toList();
    }

    // сервис для обновления определенного объекта
    public CatalogDto updateById(Integer id, CatalogDto catalogDto) {
        CatalogEntity catalogEntity = catalogRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        catalogEntity.setName(catalogDto.getName());
        catalogEntity.setDescription(catalogDto.getDescription());
        catalogEntity.setPrice(catalogDto.getPrice());
        catalogRepository.save(catalogEntity);
        return CatalogMapping.toDto(catalogEntity);
    }

    public CatalogDto deleteById(Integer id) {
        CatalogEntity catalogEntity =  catalogRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        catalogRepository.delete(catalogEntity);
        return CatalogMapping.toDto(catalogEntity);
    }

}

// Контроллер <— DTO —> Сервис <— Entity —> Репозиторий <—> БД