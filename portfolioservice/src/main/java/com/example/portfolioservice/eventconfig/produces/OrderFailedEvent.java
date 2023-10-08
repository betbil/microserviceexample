package com.example.portfolioservice.eventconfig.produces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFailedEvent {
    private UUID orderId;
    private String reason;
}