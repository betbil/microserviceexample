package com.example.portfolioservice.service;

import com.example.portfolioservice.eventconfig.consumes.BuyOrderPlacedEvent;
import com.example.portfolioservice.eventconfig.consumes.OrderProcessedEvent;
import com.example.portfolioservice.eventconfig.consumes.SellOrderPlacedEvent;
import com.example.portfolioservice.eventconfig.produces.OrderFailedEvent;
import com.example.portfolioservice.model.PortfolioItem;
import com.example.portfolioservice.repository.PortfolioItemRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortFolioCheckerService {

    private final PortfolioItemRepository portfolioService;
    private final KafkaTemplate kafkaTemplate;
    @KafkaListener(topics = "buy-order-placed", groupId = "portfolio-service")
    public void handleBuyOrderRequest(BuyOrderPlacedEvent event) {
        log.debug("handleBuyOrderRequest request: {}", event);
        log.info("handleBuyOrderRequest buy-order-placed-c1 request: {}", event);
        this.kafkaTemplate.send("buy-order-placed-c1", event.getOrderId().toString(), event);
    }

    @KafkaListener(topics = "sell-order-placed", groupId = "portfolio-service")
    public void handleSellOrderRequest(SellOrderPlacedEvent event) {
        log.debug("handleSellOrderRequest request: {}", event);
        this.kafkaTemplate.send("sell-order-placed-c1", event.getOrderId().toString(), event);
        //TODO OPEN BELOW STARTS TODOBETUL
        /*
        log.debug("handleSellOrderRequest request: {}", event);
        //check id the seller owns the stockid
        boolean portfolioExists = !(this.portfolioService.findByStockIdAndUserId(event.getUserId(), event.getStockId()).isEmpty());
        if (portfolioExists){
            //if yes, announce valid sell order
            log.info("handleSellOrderRequest sell-order-placed-c1 request: {}", event);
            this.kafkaTemplate.send("sell-order-placed-c1", event.getOrderId().toString(), event);
        }else{
            //if no, announce order fail event
            var failedEvent = OrderFailedEvent.builder().orderId(event.getOrderId()).reason("User does not own the stock").build();
            log.info("handleSellOrderRequest order-failed request: {}", failedEvent);
            this.kafkaTemplate.send("order-failed", event.getOrderId().toString(), failedEvent);
        }

         */
        //TODO OPEN BELOW STARTS
    }

    @KafkaListener(topics = "order-processed", groupId = "portfolio-service")
    @Transactional //TODO: check if this is needed
    public void handleOrderProcessedEvent(OrderProcessedEvent orderProcessedEvent) {
        log.debug("handleOrderProcessedEvent request: {}", orderProcessedEvent);
        Optional<PortfolioItem> sellerPortfolio
                = this.portfolioService.findFirstByUserIdAndStockId(orderProcessedEvent.getSellerId(), orderProcessedEvent.getStockId());
        if (sellerPortfolio.isPresent()){
            log.info("portfolio item {} deleted", sellerPortfolio.get());
            this.portfolioService.delete(sellerPortfolio.get());
        }
        boolean buyerAlreadyExists =
                this.portfolioService.findFirstByUserIdAndStockId(orderProcessedEvent.getBuyerId(), orderProcessedEvent.getStockId()).isPresent();
        if (buyerAlreadyExists){
            log.info("Buyer portfolio item buyerID {} and stockID {} already exits so ignore received orderProcessedEvent",
                    orderProcessedEvent.getBuyerId(), orderProcessedEvent.getStockId());
        }else{
            PortfolioItem buyerPortFolio = PortfolioItem.builder().
                    userId(orderProcessedEvent.getBuyerId()).
                    stockId(orderProcessedEvent.getStockId()).build();
            this.portfolioService.save(buyerPortFolio);
            log.info("buyer portfolio item {} added", buyerPortFolio);
        }

        log.info("handleOrderProcessedEvent request: {} processed", orderProcessedEvent);
    }
}


