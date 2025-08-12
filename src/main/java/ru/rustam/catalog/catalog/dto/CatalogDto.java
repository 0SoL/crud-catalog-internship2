package ru.rustam.catalog.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CatalogDto {
    int id;
    private String name;
    private String description;
    private double price;



}
