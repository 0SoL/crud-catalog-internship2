package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CatalogDto {
    private int id;
    private String name;
    private String description;
    private double price;
}


