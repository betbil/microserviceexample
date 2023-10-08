package com.example.portfolioservice.eventconfig.consumes;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

public class MultiTopicJsonDeserializer implements Deserializer<Object> {

    private final JsonDeserializer<BuyOrderPlacedEvent> buyOrderDeserializer = new JsonDeserializer<>(BuyOrderPlacedEvent.class);
    private final JsonDeserializer<SellOrderPlacedEvent> sellOrderDeserializer = new JsonDeserializer<>(SellOrderPlacedEvent.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        buyOrderDeserializer.configure(configs, isKey);
        sellOrderDeserializer.configure(configs, isKey);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        if ("buy-order-placed".equals(topic)) {
            return buyOrderDeserializer.deserialize(topic, data);
        } else if ("sell-order-placed".equals(topic)) {
            return sellOrderDeserializer.deserialize(topic, data);
        }
        // Add more topics as needed.
        // Also consider adding error handling for unrecognized topics.
        return null;
    }

    @Override
    public void close() {
        buyOrderDeserializer.close();
        sellOrderDeserializer.close();
    }
}
