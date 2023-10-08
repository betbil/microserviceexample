package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
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
    private UUID cancelId;
    @Enumerated(EnumType.STRING)
    private OrderRequestType requestType;
    @Enumerated(EnumType.STRING)
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

    public void updateBy(SellOrderRequest req){
        this.userId = req.getUserId();
        this.stockCode = req.getStockCode();
        this.stockId = req.getStockId();
        this.requestType = OrderRequestType.SELL;
        this.status = OrderStatusType.PENDING;
    }

    public void updateForCancel(UUID uuid, Integer userId){
        this.cancelId = uuid;
        this.userId = userId;
        this.requestType = OrderRequestType.CANCEL;
        this.status = OrderStatusType.PENDING;
    }
}
