package com.example.portfolioservice.eventconfig.consumes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyOrderPlacedEvent {
    private String stockCode;
    private Integer userId;
    private UUID orderId;
}