package com.example.orderservice.repository;

import com.example.orderservice.model.BuyOrder;
import com.example.orderservice.model.OrderStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuyOrderRepository  extends JpaRepository<BuyOrder, UUID> {
    Optional<BuyOrder> findFirstByStatusOrderByRequestTimeAsc(OrderStatusType status);

}
