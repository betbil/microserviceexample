package com.example.stockservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("app")
@Data
public class ApplicationProperties {
    private boolean updatedb;
}