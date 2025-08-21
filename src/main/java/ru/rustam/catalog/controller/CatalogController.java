package ru.rustam.catalog.controller;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.FilteredCatalogDto;
import ru.rustam.catalog.dto.UpdateCatalogDto;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.service.CatalogService;

import javax.swing.*;
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

    @PostMapping("/search")
    public Page<CatalogDto> search(@RequestBody FilteredCatalogDto filter) {
        return catalogService.search(filter);
    }

    @PostMapping("/newsearch")
    public List<CatalogDto> searchNew(@RequestBody FilteredCatalogDto filter) {
        return catalogService.searchnew(filter);
    }

    @PutMapping("/{id}")
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