package ru.rustam.catalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryDto {
    @Size(max = 25)
    @NotNull
    private String name;
}
