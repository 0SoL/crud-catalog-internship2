package ru.rustam.catalog.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.UpdateCatalogDto;
import ru.rustam.catalog.entity.FileEntity;
import ru.rustam.catalog.mapper.CatalogMapper;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.repository.CatalogRepository;
import ru.rustam.catalog.repository.FileRepository;

import java.util.List;

@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final FileRepository fileRepository;
    private final CatalogMapper catalogMapper;

    @Autowired
    public CatalogService(CatalogRepository catalogRepository, FileRepository fileRepository, CatalogMapper catalogMapper) {
        this.catalogRepository = catalogRepository;
        this.fileRepository = fileRepository;
        this.catalogMapper = catalogMapper;
    }

    // сервис для создания объекта
    public CatalogDto create(CreateCatalogDto createCatalogDto) {
        List<FileEntity> files = fileRepository.findAllById(createCatalogDto.getImagesIds());
        CatalogEntity catalogEntity = catalogRepository.save(catalogMapper.toEntity(createCatalogDto));

        for (FileEntity file : files) {
            if (file.getCatalog() != null) {
                throw new IllegalArgumentException("Файл уже принадлежит какой то карточке: " + file.getName());
            }
            file.setCatalog(catalogEntity);
        }
        fileRepository.saveAll(files);
        catalogEntity.setImages(files);
        return catalogMapper.toDto(catalogEntity);
        // отдельный create для изображении, потом уже связывать с каталогом
        // перенести верхний сервис в отдельный сервис
        // два контроела , никуда не привязывается, при создании каталога из контроелар файла получаем айди для контроела продукта и так и привязываем
    }

    // сервис для поиска объекта по id
    public CatalogDto findById(Integer id) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        return catalogMapper.toDto(catalogEntity);
    }
    // сервисч для вывода всех объектов
    // сервисч для вывода всех объектов
    public List<CatalogDto> findAll() {
        return catalogRepository.findPrimaryImage()
                .stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    // сервис для обновления определенного объекта
    @Transactional
    public CatalogDto updateById(Integer id, UpdateCatalogDto updateCatalogDto) {
        // проверял добавление изображения и заметил что у меня просто стирает остальные данные
        CatalogEntity catalogEntity = getCatalogEntity(id);
        if (updateCatalogDto.getNewImageIds() != null && !updateCatalogDto.getNewImageIds().isEmpty()) {
            List<FileEntity> files = fileRepository.findAllById(updateCatalogDto.getNewImageIds());
            for (FileEntity file : files) {
                file.setCatalog(catalogEntity);
            }
            catalogEntity.getImages().addAll(files);
        }
        if (updateCatalogDto.getName() != null && !updateCatalogDto.getName().isEmpty()) {
            catalogEntity.setName(updateCatalogDto.getName());
        }
        if (updateCatalogDto.getDescription() != null && !updateCatalogDto.getDescription().isEmpty()) {
            catalogEntity.setDescription(updateCatalogDto.getDescription());
        }
        if (updateCatalogDto.getPrice() != null) {
            catalogEntity.setPrice(updateCatalogDto.getPrice());
        }
        if (updateCatalogDto.getPrimaryImage() != null) {
            catalogEntity.setPrimaryImage(updateCatalogDto.getPrimaryImage());
        }
        catalogRepository.save(catalogEntity);
        return catalogMapper.toDto(catalogEntity);
    }


    public void deleteById(Integer id) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        catalogRepository.delete(catalogEntity);
        catalogMapper.toDto(catalogEntity);
    }


    private CatalogEntity getCatalogEntity(Integer id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

}

// Контроллер <— DTO —> Сервис <— Entity —> Репозиторий <—> БД