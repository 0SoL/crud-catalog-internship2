package ru.rustam.catalog.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.UpdateCatalogDto;
import ru.rustam.catalog.service.CatalogService;

import java.util.List;

@RestController
@RequestMapping("/product")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogDto create(@Valid @RequestBody CreateCatalogDto createCatalogDto) {
        return catalogService.create(createCatalogDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CatalogDto findById(@PathVariable("id") Integer id) {
        return catalogService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CatalogDto> findAll() {
        return catalogService.findAll();
    }

    @PatchMapping("/{id}") // СПРОСИТЬ ПРО PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CatalogDto updateById(@PathVariable("id") Integer id,
                                 @Valid @RequestBody UpdateCatalogDto updateCatalogDto) {
        return catalogService.updateById(id, updateCatalogDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        catalogService.deleteById(id);
    }
}


// Для создания создавать new Dto отдельный ,
// Integer ,
// два метода одинаковые названияч,
// постман ,
// просто класс для маппинга вместо интерфейс(сделать его бином @Mapper)
// маппер, прайваты ,
// рефакторить код добавить методы,
// ознакомится со stream()


// 13 августа , yml , мерджить , убрать validated. yml noun.

// Task. Добавление изображение, когда все товары выводим , показываем путь к мейн фото. Когда к определенному , все фотографии все пути этого товара.
// ManyToOne. Улучшить с метод с фотками, эндпойт. Изменить фотографию методы и тд.
