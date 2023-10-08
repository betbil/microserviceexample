package com.example.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellOrderRequest {
    private Integer userId; //TODO: JWT eklediğinde jwtden alınacak uradan alınmayacak
    private Integer stockId;
    private String stockCode;
}
