package ru.rustam.catalog.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.FilteredCatalogDto;
import ru.rustam.catalog.dto.UpdateCatalogDto;
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

    @Operation(
            summary = "Создать товар",
            description = "Создает товар в базе данных"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Создан"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogDto create(@Valid @RequestBody CreateCatalogDto createCatalogDto) {
        return catalogService.create(createCatalogDto);
    }

    @Operation(
            summary = "Получить товар",
            description = "Возвращает товар по указаному id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ок"),
            @ApiResponse(responseCode = "404", description = "Не найден")
    })
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
    public Page<CatalogDto> searchNew(@RequestBody FilteredCatalogDto filter,
                                      Pageable pageable) {
        return catalogService.searchnew(filter, pageable);
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

// 1 Интеграционные тесты, проверка каждого контролера. Проверять ответы и сохранение в бд . framework Test Containers!! (модульные тесты ознакомится), чекать с помощью assert
// 2 Spring Security , авторизация и тд
// 3 Межсервисное взаимодействие. Логи остатки MockMVc