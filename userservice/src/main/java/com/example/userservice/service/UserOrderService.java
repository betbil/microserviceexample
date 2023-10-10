package com.example.userservice.service;

import com.example.userservice.eventconfig.consumes.OrderFailedEvent;
import com.example.userservice.eventconfig.consumes.OrderProcessedEvent;
import com.example.userservice.eventconfig.produces.BuyOrderPlacedEvent;
import com.example.userservice.eventconfig.produces.CancelOrderPlacedEvent;
import com.example.userservice.eventconfig.produces.SellOrderPlacedEvent;
import com.example.userservice.exceptions.CancelRequestException;
import com.example.userservice.exceptions.InvalidRequestException;
import com.example.userservice.model.*;
import com.example.userservice.repository.UserOrderRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.SocketException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrderService {

    private final KafkaTemplate kafkaTemplate;
    private final UserOrderRequestRepository userOrderRequestRepository;
    private final StockClient stockClient;

    public boolean checkStockExists(String stockCode) {
        log.debug("stockExists request: {}", stockCode);
        StockStatus stockStatus = null;
        try{
            stockStatus = this.stockClient.getStockStatus(stockCode);
        }catch (Exception ex){
            log.error("Error while checking stock status: {}", ex.getMessage());
        }

        if (stockStatus == null || !stockStatus.isExists()) {
            throw new InvalidRequestException("Stock does not exist");
        }
        return true;
    }

    @Transactional
    public void buyOrder(BuyOrderRequest buyOrderRequest) {
        log.debug("buyOrder request: {}", buyOrderRequest);
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
        log.debug("sellOrder request: {}", sellOrderRequest);
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
        log.debug("cancelOrder request: orderID {}, userID {}", orderID, userId);
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
        log.debug("handleOrderFailedEvent request: {}", orderFailedEvent);
        Optional<UserOrderRequest> failedOrder = this.userOrderRequestRepository.findById(orderFailedEvent.getOrderId());
        if (failedOrder.isPresent()){
           if (failedOrder.get().getStatus() != OrderStatusType.PENDING){
                log.info("Order already processed: {} so fail ignored", orderFailedEvent.getOrderId());
                return;
            }
            failedOrder.get().setStatus(OrderStatusType.FAILED);
            failedOrder.get().setStatusDescription(orderFailedEvent.getReason());
            this.userOrderRequestRepository.save(failedOrder.get());
        }else{
            log.info("Order not found: {} so cancel ignored", orderFailedEvent.getOrderId());
        }
    }

    @KafkaListener(topics = "order-processed", groupId = "user-service")
    @Transactional //TODO: check if this is needed
    public void handleOrderProcessedEvent(OrderProcessedEvent orderProcessedEvent) {
        log.debug("handleOrderProcessedEvent request: {}", orderProcessedEvent);

        Optional<UserOrderRequest> buyOrder = this.userOrderRequestRepository.findById(orderProcessedEvent.getBuyOrderID());
        Optional<UserOrderRequest> sellOrder = this.userOrderRequestRepository.findById(orderProcessedEvent.getSellOrderID());

        if (buyOrder.isPresent()){
            if (!buyOrder.get().getStatus().equals(OrderStatusType.PENDING)){
                log.info("Buy Order {} not pending {} so completed order ignored", orderProcessedEvent.getBuyOrderID(),
                        buyOrder.get().getStatus());
            }else{
                buyOrder.get().setStatus(OrderStatusType.COMPLETED);
                this.userOrderRequestRepository.save(buyOrder.get());
            }
        }

        if (sellOrder.isPresent()){
            if (!sellOrder.get().getStatus().equals(OrderStatusType.PENDING)){
                log.info("Sell Order {} not pending {} so completed order ignored", orderProcessedEvent.getSellerId(),
                        sellOrder.get().getStatus());
            }else{
                sellOrder.get().setStatus(OrderStatusType.COMPLETED);
                this.userOrderRequestRepository.save(sellOrder.get());
            }
        }
        log.info("handleOrderProcessedEvent request: {} processed", orderProcessedEvent);
    }
}
