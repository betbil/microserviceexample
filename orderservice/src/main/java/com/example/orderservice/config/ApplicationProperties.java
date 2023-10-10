package com.example.orderservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
@Data
public class ApplicationProperties {
    private String redisUrl;
    private boolean createSellerData;
}
