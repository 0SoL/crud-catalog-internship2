package ru.rustam.catalog.generator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.rustam.catalog.dto.OrderDto;
import ru.rustam.catalog.producer.OrderProducer;

@Component
public class OrderGenerator implements ApplicationRunner {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final OrderProducer orderProducer;

    public OrderGenerator(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final int total = 5000;
        for (int i = 0; i < total; i++) {
            int productId = i;
            int userId = i;

            OrderDto orderDto = new OrderDto();
            orderDto.setProductId(productId);
            orderDto.setUserId(userId);

            orderProducer.send("order.2part", orderDto);
        }
        System.out.println("Sent " + total + " orders");
    }
}
// thread pool