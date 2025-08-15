package ru.rustam.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-path}")
    private String folderPath;

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
    public List<CatalogDto> findAll() {
        return catalogRepository.findPrimaryImage()
                .stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    // сервис для обновления определенного объекта
    public CatalogDto updateById(Integer id, UpdateCatalogDto updateCatalogDto) {
        CatalogEntity catalogEntity = getCatalogEntity(id);
        catalogEntity.setName(updateCatalogDto.getName());
        catalogEntity.setDescription(updateCatalogDto.getDescription());
        catalogEntity.setPrice(updateCatalogDto.getPrice());
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