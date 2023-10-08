package com.example.userservice.service;

import com.example.userservice.eventconfig.consumes.OrderFailedEvent;
import com.example.userservice.eventconfig.produces.BuyOrderPlacedEvent;
import com.example.userservice.eventconfig.produces.CancelOrderPlacedEvent;
import com.example.userservice.eventconfig.produces.SellOrderPlacedEvent;
import com.example.userservice.exceptions.CancelRequestException;
import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.model.OrderStatusType;
import com.example.userservice.model.SellOrderRequest;
import com.example.userservice.model.UserOrderRequest;
import com.example.userservice.repository.UserOrderRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserOrderService {

    private final KafkaTemplate kafkaTemplate;
    private final UserOrderRequestRepository userOrderRequestRepository;
    @Transactional
    public void buyOrder(BuyOrderRequest buyOrderRequest) {
        //save to db
        UserOrderRequest userOrderRequest = new UserOrderRequest();
        userOrderRequest.updateBy(buyOrderRequest);
        this.userOrderRequestRepository.save(userOrderRequest);
        //publish event
        //TODO: catch exception and rollback db
        this.kafkaTemplate.send("buy-order-placed", userOrderRequest.getId().toString(), BuyOrderPlacedEvent.builder()
                .stockCode(buyOrderRequest.getStockCode())
                .userId(buyOrderRequest.getUserId())
                .orderId(userOrderRequest.getId())
                .build());
    }

    @Transactional
    public void sellOrder(SellOrderRequest sellOrderRequest) {
        //save to db
        UserOrderRequest userOrderRequest = new UserOrderRequest();
        userOrderRequest.updateBy(sellOrderRequest);
        this.userOrderRequestRepository.save(userOrderRequest);
        //publish event
        //TODO: catch exception and rollback db
        this.kafkaTemplate.send("sell-order-placed", userOrderRequest.getId().toString(), SellOrderPlacedEvent.builder()
                .stockCode(sellOrderRequest.getStockCode())
                .userId(sellOrderRequest.getUserId())
                .stockId(sellOrderRequest.getStockId())
                .orderId(userOrderRequest.getId())
                .build());
    }

    @Transactional
    public void cancelOrder(String orderID, Integer userId) {
        //TODO: userid jwt den alacak şekide değiştir ve userid içinde check ekle
        UUID uuid = UUID.fromString(orderID);
       Optional<UserOrderRequest> userOrderRequest = this.userOrderRequestRepository.findById(uuid);
        if(userOrderRequest.isPresent()){
            if (!userOrderRequest.get().getUserId().equals(userId)){
                throw new CancelRequestException("User does not own the order");
            }
            if (userOrderRequest.get().getStatus() != OrderStatusType.PENDING){
                throw new CancelRequestException("Order is not pending");
            }
            UserOrderRequest cancelUserOrderRequest = new UserOrderRequest();
            cancelUserOrderRequest.updateForCancel(uuid, userId);
            this.userOrderRequestRepository.save(cancelUserOrderRequest);
            //send cancel event to kafka
            //publish event
            //TODO: catch exception and rollback db
            this.kafkaTemplate.send("cancel-order-placed", cancelUserOrderRequest.getId().toString(), CancelOrderPlacedEvent.builder()
                    .orderId(cancelUserOrderRequest.getId())
                    .cancelId(cancelUserOrderRequest.getCancelId())
                    .userId(userId)
                    .build());

        }else{
            throw new CancelRequestException("Order not found");
        }
    }

    //user-service
    @KafkaListener(topics = "order-failed", groupId = "user-service")
    public void handleOrderFailedEvent(OrderFailedEvent orderFailedEvent) {
        Optional<UserOrderRequest> failedOrder = this.userOrderRequestRepository.findById(orderFailedEvent.getOrderId());
        if (failedOrder.isPresent()){
            failedOrder.get().setStatus(OrderStatusType.FAILED);
            failedOrder.get().setStatusDescription(orderFailedEvent.getReason());
            this.userOrderRequestRepository.save(failedOrder.get());
        }
        //TODO: add logging
    }
}
