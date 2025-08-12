package ru.rustam.catalog.catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rustam.catalog.catalog.dto.CatalogDto;
import ru.rustam.catalog.catalog.dto.mapping.CatalogMapping;
import ru.rustam.catalog.catalog.entity.CatalogEntity;
import ru.rustam.catalog.catalog.service.CatalogService;

import javax.xml.catalog.Catalog;
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
    public CatalogDto create(@RequestBody CatalogDto catalogDto) {
        return catalogService.create(catalogDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CatalogDto findAll(@PathVariable("id") Integer id) {
        return catalogService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CatalogDto> findAll() {
        return catalogService.findAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CatalogDto delete(@PathVariable("id") Integer id,@RequestBody CatalogDto catalogDto) {
        return catalogService.updateById(id, catalogDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        catalogService.deleteById(id);
    }
}
