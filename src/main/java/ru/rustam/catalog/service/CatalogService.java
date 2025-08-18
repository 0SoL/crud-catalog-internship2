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
import ru.rustam.catalog.exception.ImageException;
import ru.rustam.catalog.mapper.CatalogMapper;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.repository.CatalogRepository;
import ru.rustam.catalog.repository.FileRepository;

import java.awt.*;
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

    public CatalogDto create(CreateCatalogDto createCatalogDto) {
        List<FileEntity> files = fileRepository.findAllById(createCatalogDto.getImagesIds());
        CatalogEntity catalogEntity = catalogRepository.save(catalogMapper.toEntity(createCatalogDto));

        if (!createCatalogDto.getImagesIds().contains(createCatalogDto.getPrimaryImage())) {
            throw new ImageException("Primary image не найден, выберите Primary image из ваших imageIds");
        }

        FileEntity primaryImage = fileRepository.findById(createCatalogDto.getPrimaryImage())
                .orElseThrow(() -> new ImageException("Primary image не найден"));
        catalogEntity.setPrimaryImage(primaryImage);

        for (FileEntity file : files) {
            if (file.getCatalog() != null) {
                throw new ImageException("Файл уже принадлежит какой то карточке: " + file.getName());
            }
            file.setCatalog(catalogEntity);
        }
        fileRepository.saveAll(files);
        catalogEntity.setImages(files);
        return catalogMapper.toDto(catalogEntity);
    }

    public CatalogDto findById(Integer id) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        return catalogMapper.toDto(catalogEntity);
    }

    public List<CatalogDto> findAll() {
        return catalogRepository
                .findAll()
                .stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    @Transactional
    public CatalogDto updateById(Integer id, UpdateCatalogDto updateCatalogDto) {
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
            FileEntity primaryImage = fileRepository.findById(updateCatalogDto.getPrimaryImage())
                    .orElseThrow(() -> new ImageException("Primary image не найден"));
            catalogEntity.setPrimaryImage(primaryImage);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Продукт не найден!"));
    }
}

// Контроллер <— DTO —> Сервис <— Entity —> Репозиторий <—> БД