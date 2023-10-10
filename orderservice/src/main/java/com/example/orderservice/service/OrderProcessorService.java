package com.example.orderservice.service;

import com.example.orderservice.eventconfig.consumes.BuyOrderPlacedEvent;
import com.example.orderservice.eventconfig.consumes.CancelOrderPlacedEvent;
import com.example.orderservice.eventconfig.consumes.SellOrderPlacedEvent;
import com.example.orderservice.eventconfig.produces.OrderFailedEvent;
import com.example.orderservice.eventconfig.produces.OrderProcessedEvent;
import com.example.orderservice.model.BuyOrder;
import com.example.orderservice.model.OrderStatusType;
import com.example.orderservice.model.SellOrder;
import com.example.orderservice.repository.BuyOrderRepository;
import com.example.orderservice.repository.SellOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessorService {
    private final KafkaTemplate kafkaTemplate;
    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;
    private final RedissonClient redissonClient;

    @Transactional
    @KafkaListener(topics = "cancel-order-placed", groupId = "order-service")
    public void handleCancelOrderPlacedEvent(CancelOrderPlacedEvent cancelOrderPlacedEvent) {
        log.debug("handleCancelOrderPlacedEvent request: {}", cancelOrderPlacedEvent);

        Optional<BuyOrder> canceledBuyOrder = this.buyOrderRepository.findById(cancelOrderPlacedEvent.getCancelId());
        if (canceledBuyOrder.isPresent()){
            //check if order initiated by the user
            if (!canceledBuyOrder.get().getUserId().equals(cancelOrderPlacedEvent.getUserId())){
                log.info("Cancel Order initiated by wrong user: {} so cancel ignored", cancelOrderPlacedEvent.getCancelId());
                this.kafkaTemplate.send("order-failed", OrderFailedEvent.builder()
                        .orderId(cancelOrderPlacedEvent.getOrderId())
                        .reason("Cancel Order initiated by wrong user")
                        .build());
                return;
            }
            if (canceledBuyOrder.get().getStatus().equals(OrderStatusType.CANCELLED)){
                log.info("Order already canceled: {} so cancel ignored", cancelOrderPlacedEvent.getCancelId());
                return;
            }

            canceledBuyOrder.get().setStatus(OrderStatusType.CANCELLED);
            this.buyOrderRepository.save(canceledBuyOrder.get());
            log.info("Buy order canceled: {}", canceledBuyOrder.get());
        }else {
            Optional<SellOrder> cancelledSellOrder = this.sellOrderRepository.findById(cancelOrderPlacedEvent.getCancelId());
            if (cancelledSellOrder.isPresent()){
                //check if order initiated by the user
                if(!cancelledSellOrder.get().getUserId().equals(cancelOrderPlacedEvent.getUserId())){
                    log.info("Cancel Order initiated by wrong user: {} so cancel ignored", cancelOrderPlacedEvent.getCancelId());
                    this.kafkaTemplate.send("order-failed", OrderFailedEvent.builder()
                            .orderId(cancelOrderPlacedEvent.getOrderId())
                            .reason("Cancel Order initiated by wrong user")
                            .build());
                    return;
                }

                if (cancelledSellOrder.get().getStatus().equals(OrderStatusType.CANCELLED)){
                    log.info("Order already canceled: {} so cancel ignored", cancelOrderPlacedEvent.getCancelId());
                    return;
                }

                cancelledSellOrder.get().setStatus(OrderStatusType.CANCELLED);
                this.sellOrderRepository.save(cancelledSellOrder.get());
                log.info("Sell order canceled: {}", cancelledSellOrder.get());
            }else{
                log.info("Order not found: {} so cancel ignored", cancelOrderPlacedEvent.getCancelId());
                this.kafkaTemplate.send("order-failed", OrderFailedEvent.builder()
                        .orderId(cancelOrderPlacedEvent.getOrderId())
                        .reason("Order not found")
                        .build());
            }
        }
    }

    @Transactional
    @KafkaListener(topics = "buy-order-placed-c1", groupId = "order-service")
    public void handleBuyOrderPlacedEvent(BuyOrderPlacedEvent buyOrderPlacedEvent) {
        log.debug("handleBuyOrderPlacedEvent request: {}", buyOrderPlacedEvent);
        //TODO: check total count of the stock max 10
        BuyOrder buyOrder = BuyOrder.builder()
                .id(buyOrderPlacedEvent.getOrderId())
                .userId(buyOrderPlacedEvent.getUserId())
                .status(OrderStatusType.PENDING)
                .build();
        if (buyOrder.getId() == null) {
            buyOrder.setId(UUID.randomUUID());
        }
        buyOrderRepository.save(buyOrder);

        this.buyOrderRepository.save(buyOrder);
        matchOrders();

    }

    @Transactional
    @KafkaListener(topics = "sell-order-placed-c1", groupId = "order-service")
    public void handleSellOrderPlacedEvent(SellOrderPlacedEvent sellOrderPlacedEvent) {
        log.debug("handleSellOrderPlacedEvent request: {}", sellOrderPlacedEvent);
        boolean alreadyExists = this.sellOrderRepository.existsByUserIdAndStockIdAndStatus(sellOrderPlacedEvent.getUserId(), sellOrderPlacedEvent.getStockId(), OrderStatusType.PENDING);

        if (alreadyExists){
            log.info("Sell Order already exits for userID {} and stockID {} so cancel ignored", sellOrderPlacedEvent.getUserId(), sellOrderPlacedEvent.getStockId());
            this.kafkaTemplate.send("order-failed", OrderFailedEvent.builder()
                    .orderId(sellOrderPlacedEvent.getOrderId())
                    .reason("Cancel Order initiated by wrong user")
                    .build());
            return;
        }

        SellOrder sellOrder = SellOrder.builder()
                .id(sellOrderPlacedEvent.getOrderId())
                .userId(sellOrderPlacedEvent.getUserId())
                .stockId(sellOrderPlacedEvent.getStockId())
                .status(OrderStatusType.PENDING)
                .build();

        if (sellOrder.getId() == null) {
            sellOrder.setId(UUID.randomUUID());
        }

        this.sellOrderRepository.save(sellOrder);
        matchOrders();

    }

    @Transactional
    public void matchOrders() {
        RLock lock = redissonClient.getLock("matchOrders");
        Optional<BuyOrder> firstBuyOrder = Optional.empty();
        Optional<SellOrder> firstSellOrder = Optional.empty();

        try {
            if (lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                firstBuyOrder = this.buyOrderRepository.findFirstByStatusOrderByRequestTimeAsc(OrderStatusType.PENDING);
                firstSellOrder = this.sellOrderRepository.findFirstByStatusOrderByRequestTimeAsc(OrderStatusType.PENDING);
                if (firstBuyOrder.isPresent() && firstSellOrder.isPresent()) {
                    // Transfer the stock from seller to buyer
                    firstBuyOrder.get().setStatus(OrderStatusType.COMPLETED);
                    firstSellOrder.get().setStatus(OrderStatusType.COMPLETED);
                    this.buyOrderRepository.save(firstBuyOrder.get());
                    this.sellOrderRepository.save(firstSellOrder.get());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Always make sure to unlock in a finally block
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        //now send kafka event
        if (firstBuyOrder.isPresent() && firstSellOrder.isPresent()) {
            this.kafkaTemplate.send("order-processed", OrderProcessedEvent.builder()
                    .sellOrderID(firstSellOrder.get().getId())
                    .buyOrderID(firstBuyOrder.get().getId())
                    .buyerId(firstBuyOrder.get().getUserId())
                    .sellerId(firstSellOrder.get().getUserId())
                    .stockId(firstSellOrder.get().getStockId())
                    .build());
        }
    }




}
