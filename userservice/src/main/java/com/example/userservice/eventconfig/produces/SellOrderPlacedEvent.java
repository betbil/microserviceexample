package com.example.userservice.eventconfig.produces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellOrderPlacedEvent {
    private String stockCode;
    private Integer userId;
    private Integer stockId;
    private UUID orderId;
}