package ru.rustam.catalog.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.rustam.catalog.dto.OrderStatusDto;


@Service
public class CatalogConsumer {
    @KafkaListener(topics = "order.confirmed", groupId = "catalog_group")
    public void onConfirmed(OrderStatusDto orderStatusDto) {
        System.out.println("Order confirmed: " + orderStatusDto);
    }
}
