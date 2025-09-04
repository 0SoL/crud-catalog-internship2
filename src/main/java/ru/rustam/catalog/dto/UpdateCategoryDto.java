package ru.rustam.catalog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateCategoryDto {
    private String name;
}
