package ru.rustam.catalog.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
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
    public CatalogDto create(@RequestBody CreateCatalogDto createCatalogDto) {
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CatalogDto updateById(@PathVariable("id") Integer id,
                                 @RequestBody CatalogDto catalogDto) {
        return catalogService.updateById(id, catalogDto);
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