package com.example.portfolioservice.eventconfig.consumes;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class MultiTopicJsonDeserializer implements Deserializer<Object> {

    private final ExtendedJsonDeserializer<BuyOrderPlacedEvent> buyOrderDeserializer = new ExtendedJsonDeserializer<>(BuyOrderPlacedEvent.class);
    private final ExtendedJsonDeserializer<SellOrderPlacedEvent> sellOrderDeserializer = new ExtendedJsonDeserializer<>(SellOrderPlacedEvent.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        buyOrderDeserializer.configure(configs, isKey);
        sellOrderDeserializer.configure(configs, isKey);


        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        //buyOrderDeserializer.setTypeResolver(typeMapper);
        //sellOrderDeserializer.setTypeResolver(typeMapper);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        Object result = null;
        if ("buy-order-placed".equals(topic)) {
            result = buyOrderDeserializer.deserialize(topic, data);
        } else if ("sell-order-placed".equals(topic)) {
            result = sellOrderDeserializer.deserialize(topic, data);
        }
        String jsonString = new String(data, StandardCharsets.UTF_8);
        log.debug("Deserialized object: {}", result);
        log.debug("received json: {}", jsonString);

        return result;
    }


    @Override
    public void close() {
        buyOrderDeserializer.close();
        sellOrderDeserializer.close();
    }
}
