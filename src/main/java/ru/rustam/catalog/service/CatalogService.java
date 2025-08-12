package ru.rustam.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.mapping.CatalogMapper;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.repository.CatalogRepository;

import java.util.List;

@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    // сервис для создания объекта
    public CatalogDto create(CreateCatalogDto createCatalogDto) {
        CatalogEntity catalogEntity = catalogRepository
                .save(CatalogMapper.toEntity(createCatalogDto));
        return CatalogMapper.toDto(catalogEntity);
    }

    // сервис для поиска объекта по id
    public CatalogDto findById(Integer id) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        return CatalogMapper.toDto(catalogEntity);
    }

    // сервисч для вывода всех объектов
    public List<CatalogDto> findAll() {
        return catalogRepository.findAll()
                .stream() // Окей в моем понимании стрим, позволяет превращать колекции в потом данных, так же позволяет с ними взаимодействовать, к примеру если мне нужно отфильровать и вывести только товары с ценником 1200
                .map(CatalogMapper::toDto)
                .toList();
    }

    // сервис для обновления определенного объекта
    public CatalogDto updateById(Integer id, CatalogDto catalogDto) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        catalogEntity.setName(catalogDto.getName());
        catalogEntity.setDescription(catalogDto.getDescription());
        catalogEntity.setPrice(catalogDto.getPrice());
        catalogRepository.save(catalogEntity);
        return CatalogMapper.toDto(catalogEntity);
    }

    public void deleteById(Integer id) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        catalogRepository.delete(catalogEntity);
        CatalogMapper.toDto(catalogEntity);
    }

    private CatalogEntity getCatalogEntity(Integer id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}

// Контроллер <— DTO —> Сервис <— Entity —> Репозиторий <—> БД