package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class UserOrderRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Column(nullable = false)
    private Integer userId;
    private Integer stockId;
    private String stockCode;
    private OrderRequestType requestType;
    private OrderStatusType status;
    private String statusDescription; // for error messages
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestTime;
    @PrePersist
    public void prePersist() {
        this.requestTime = LocalDateTime.now();
    }
    public void updateBy(BuyOrderRequest req){
        this.userId = req.getUserId();
        this.stockCode = req.getStockCode();
        this.requestType = OrderRequestType.BUY;
        this.status = OrderStatusType.PENDING;
    }
}
