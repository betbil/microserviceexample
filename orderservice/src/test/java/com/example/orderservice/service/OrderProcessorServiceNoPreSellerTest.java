package com.example.orderservice.service;

import com.example.orderservice.eventconfig.consumes.BuyOrderPlacedEvent;
import com.example.orderservice.model.BuyOrder;
import com.example.orderservice.model.OrderStatusType;
import com.example.orderservice.repository.BuyOrderRepository;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.profiles.active=nosellertest"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = {"cancel-order-placed", "buy-order-placed-c1", "sell-order-placed-c1"})
public class OrderProcessorServiceNoPreSellerTest {

    @Autowired
    private OrderProcessorService orderProcessorService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private BuyOrderRepository buyOrderRepository;

    @Autowired
    private Environment environment;

    @ClassRule
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    /*
    @MockBean
    private SellOrderRepository sellOrderRepository;
*/

    @Test
    public void testBuyOrderWhenNoSellerIntegrationScenario() throws InterruptedException {
        System.out.println("JUnit version: " + junit.runner.Version.id());
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("Active Profiles: " + String.join(", ", activeProfiles));


        UUID orderId = UUID.randomUUID();
        BuyOrderPlacedEvent event = BuyOrderPlacedEvent.builder()
                .orderId(orderId)
                .userId(1)
                .stockCode("APPLNoSeller")
                .build();

        // Produce the event to Kafka
        this.kafkaTemplate.send("buy-order-placed-c1", event);

        // Wait for asynchronous processing
        Thread.sleep(5000);  // wait 5 seconds to ensure the Kafka listener has processed the message

        // Verify that the buy order is saved in the database
        Optional<BuyOrder> buyOrderOptional = buyOrderRepository.findById(orderId);
        assertTrue(buyOrderOptional.isPresent());
        assertEquals(OrderStatusType.PENDING, buyOrderOptional.get().getStatus());
        assertEquals(1, buyOrderOptional.get().getUserId().longValue());
    }

}
