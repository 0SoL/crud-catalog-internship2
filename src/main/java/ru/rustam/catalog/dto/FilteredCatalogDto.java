package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FilteredCatalogDto {
    private String name;
    private String description;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean hasImages;
    private Integer categoryId;
    private Integer size;
    private Integer page;
}
