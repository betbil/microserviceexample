package com.example.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("app")
@Data
public class ApplicationProperties {
    private StockService stockservice;
    @Data
    public static class StockService {
        private String stockServiceUrl;
    }
}