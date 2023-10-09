package com.example.portfolioservice.eventconfig.consumes;

import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class ExtendedJsonDeserializer<T> extends JsonDeserializer<T> {
    public ExtendedJsonDeserializer(Class<T> targetType) {
        super(targetType);
        //super(targetType, JacksonUtils.enhancedObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }

    public void setTypeResolver(Jackson2JavaTypeMapper typeResolver) {
        super.setTypeMapper(typeResolver);
    }

}