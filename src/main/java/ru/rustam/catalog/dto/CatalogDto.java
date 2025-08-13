package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CatalogDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
}


