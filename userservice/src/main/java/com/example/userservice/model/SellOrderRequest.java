package com.example.userservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SellOrderRequest {
    private Integer userId; //TODO: JWT eklediğinde jwtden alınacak uradan alınmayacak
    private Integer stockId;
    private String stockCode;
}
