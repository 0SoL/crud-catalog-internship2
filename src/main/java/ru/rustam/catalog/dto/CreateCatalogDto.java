package ru.rustam.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCatalogDto {
    @NotBlank
    private String name;
    private String description;
    @Positive
    private double price;
}
