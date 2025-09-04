package ru.rustam.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rustam.catalog.dto.OrderDto;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody OrderDto orderDto) {
        kafkaTemplate.send("order.create", orderDto);
        return ResponseEntity.ok("Order sent to Kafka: " + orderDto);
    }
}
