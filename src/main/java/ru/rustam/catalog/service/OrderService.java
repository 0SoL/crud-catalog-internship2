package ru.rustam.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.rustam.catalog.dto.OrderDto;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public void sendOrder(OrderDto orderDto) {
        kafkaTemplate.send("orders.create",orderDto);
    }

}
