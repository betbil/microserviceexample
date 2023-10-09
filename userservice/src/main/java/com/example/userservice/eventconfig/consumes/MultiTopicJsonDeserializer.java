package com.example.userservice.eventconfig.consumes;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class MultiTopicJsonDeserializer implements Deserializer<Object> {

    private final JsonDeserializer<OrderFailedEvent> orderFailedDeserializer = new JsonDeserializer<>(OrderFailedEvent.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        orderFailedDeserializer.configure(configs, isKey);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        Object result = null;
        if ("order-failed".equals(topic)) {
            result = orderFailedDeserializer.deserialize(topic, data);
        }
        // Add more topics as needed.
        // TODO: Also consider adding error handling for unrecognized topics.
        String jsonString = new String(data, StandardCharsets.UTF_8);
        log.debug("Deserialized object: {}", result);
        log.debug("received json: {}", jsonString);
        return result;
    }

    @Override
    public void close() {
        orderFailedDeserializer.close();
    }
}
