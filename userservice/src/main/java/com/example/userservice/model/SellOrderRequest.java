package com.example.userservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SellOrderRequest {
    private Integer userId; //TODO: JWT eklediğinde jwtden alınacak uradan alınmayacak
    @Min(value = 1, message = "Stock id must be greater than 0")
    private Integer stockId;
    @NotEmpty
    private String stockCode;
}
