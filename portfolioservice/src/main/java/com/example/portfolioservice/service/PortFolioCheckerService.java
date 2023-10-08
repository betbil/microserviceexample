package com.example.portfolioservice.service;

import com.example.portfolioservice.eventconfig.consumes.BuyOrderPlacedEvent;
import com.example.portfolioservice.eventconfig.consumes.SellOrderPlacedEvent;
import com.example.portfolioservice.eventconfig.produces.OrderFailedEvent;
import com.example.portfolioservice.repository.PortfolioItemRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortFolioCheckerService {

    private final PortfolioItemRepository portfolioService;
    private final KafkaTemplate kafkaTemplate;
    @KafkaListener(topics = "buy-order-placed", groupId = "portfolio-service")
    public void handleBuyOrderRequest(BuyOrderPlacedEvent event) {
        log.debug("PortFolioCheckerService.handleBuyOrderRequest request: {}", event);
        log.info("PortFolioCheckerService.handleBuyOrderRequest buy-order-placed-c1 request: {}", event);
        this.kafkaTemplate.send("buy-order-placed-c1", event.getOrderId().toString(), event);
    }

    @KafkaListener(topics = "sell-order-placed", groupId = "portfolio-service")
    public void handleSellOrderRequest(SellOrderPlacedEvent event) {
        log.debug("PortFolioCheckerService.handleSellOrderRequest request: {}", event);
        //check id the seller owns the stockid
        boolean portfolioExists = this.portfolioService.findByStockIdAndUserId(event.getUserId(), event.getStockId()).isEmpty();
        if (portfolioExists){
            //if yes, announce valid sell order
            log.info("PortFolioCheckerService.handleSellOrderRequest sell-order-placed-c1 request: {}", event);
            this.kafkaTemplate.send("sell-order-placed-c1", event.getOrderId().toString(), event);
        }else{
            //if no, announce order fail event
            var failedEvent = OrderFailedEvent.builder().orderId(event.getOrderId()).reason("User does not own the stock").build();
            log.info("PortFolioCheckerService.handleSellOrderRequest order-failed request: {}", failedEvent);
            this.kafkaTemplate.send("order-failed", event.getOrderId().toString(), failedEvent);
        }
    }
}


