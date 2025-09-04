package ru.rustam.catalog.producer;

import org.aspectj.weaver.ast.Or;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.rustam.catalog.dto.OrderDto;

@Service
public class OrderProducer {
    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void send(String topic, OrderDto orderDto) {
        kafkaTemplate.send(topic, orderDto);
    }
}
