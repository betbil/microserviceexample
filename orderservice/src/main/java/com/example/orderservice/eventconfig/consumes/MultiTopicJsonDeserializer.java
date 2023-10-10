package com.example.orderservice.eventconfig.consumes;

import com.example.orderservice.eventconfig.produces.OrderFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class MultiTopicJsonDeserializer implements Deserializer<Object> {

    private final JsonDeserializer<BuyOrderPlacedEvent> buyOrderPlacedEventDeserializer = new JsonDeserializer<>(BuyOrderPlacedEvent.class);
    private final JsonDeserializer<SellOrderPlacedEvent> sellOrderPlacedEventDeserializer = new JsonDeserializer<>(SellOrderPlacedEvent.class);
    private final JsonDeserializer<CancelOrderPlacedEvent> cancelOrderPlacedEventDeserializer = new JsonDeserializer<>(CancelOrderPlacedEvent.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        buyOrderPlacedEventDeserializer.configure(configs, isKey);
        sellOrderPlacedEventDeserializer.configure(configs, isKey);
        cancelOrderPlacedEventDeserializer.configure(configs, isKey);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        Object result = null;
        if ("buy-order-placed-c1".equals(topic)) {
            result = buyOrderPlacedEventDeserializer.deserialize(topic, data);
        } else if ("sell-order-placed-c1".equals(topic)) {
            result = sellOrderPlacedEventDeserializer.deserialize(topic, data);
        } else if ("cancel-order-placed".equals(topic)) {
            result = cancelOrderPlacedEventDeserializer.deserialize(topic, data);
        }
        String jsonString = new String(data, StandardCharsets.UTF_8);
        log.debug("Deserialized object: {}", result);
        log.debug("received json: {}", jsonString);

        return result;
    }

    @Override
    public void close() {
        buyOrderPlacedEventDeserializer.close();
        sellOrderPlacedEventDeserializer.close();
        cancelOrderPlacedEventDeserializer.close();
    }
}
