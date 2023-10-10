package com.example.orderservice.repository;


import com.example.orderservice.model.BuyOrder;
import com.example.orderservice.model.OrderStatusType;
import com.example.orderservice.model.SellOrder;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SellOrderRepository extends JpaRepository<SellOrder, UUID> {
     boolean existsByUserIdAndStockIdAndStatus(Integer userId, Integer stockId, OrderStatusType status);

    Optional<SellOrder> findFirstByStatusOrderByRequestTimeAsc(OrderStatusType status);



 }
