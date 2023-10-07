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
@Table(name = "buyorder", indexes = @Index(columnList = "requestTime"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOrder {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private Integer userId;
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestTime;
}