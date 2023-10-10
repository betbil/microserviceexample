package com.example.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sellorder", indexes = @Index(columnList = "requestTime"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellOrder {
    @Id
    private UUID id;
    private Integer userId;
    private Integer stockId;
    @Enumerated(EnumType.STRING)
    private OrderStatusType status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestTime;
    @PrePersist
    public void prePersist() {
        this.requestTime = LocalDateTime.now();
    }
}
