package com.example.portfolioservice.eventconfig.consumes;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderProcessedEvent {
    private UUID buyOrderID;
    private UUID sellOrderID;
    private Integer sellerId;
    private Integer buyerId;
    private Integer stockId;
}