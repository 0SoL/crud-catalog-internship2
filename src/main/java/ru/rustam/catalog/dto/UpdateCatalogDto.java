package ru.rustam.catalog.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCatalogDto {
    private Integer id;
    @Size(min = 3, max = 100, message = "Название должно иметь не меньше {min} символов и не больше {max}")
    private String name;
    @Size(max = 255, message = "Описание не должно иметь больше 200 символов")
    private String description;
    @Positive(message = "Цена не может быть отрицательной")
    @Digits(integer=8, fraction = 2)
    private BigDecimal price;
}
