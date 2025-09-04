package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Integer userId;
    private Integer productId;
}
