package ru.rustam.catalog.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDto {
    private Integer orderId;
    private String status; // CONFIRMED / REJECTED / etc
    private Integer userId;
    private Integer productId;
    private Long timestamp;
}
