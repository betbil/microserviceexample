package com.example.userservice.eventconfig.consumes;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

public class MultiTopicJsonDeserializer implements Deserializer<Object> {

    private final JsonDeserializer<OrderFailedEvent> orderFailedDeserializer = new JsonDeserializer<>(OrderFailedEvent.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        orderFailedDeserializer.configure(configs, isKey);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        if ("order-failed".equals(topic)) {
            return orderFailedDeserializer.deserialize(topic, data);
        }
        // Add more topics as needed.
        // Also consider adding error handling for unrecognized topics.
        return null;
    }

    @Override
    public void close() {
        orderFailedDeserializer.close();
    }
}
