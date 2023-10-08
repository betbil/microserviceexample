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
public class CancelOrderPlacedEvent {
    private UUID orderId;
    private UUID cancelId;
    private Integer userId;
}