package com.example.portfolioservice.service;

import com.example.portfolioservice.model.PortfolioItem;
import com.example.portfolioservice.repository.PortfolioItemRepository;
import lombok.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortFolioCheckerService {

    private final PortfolioItemRepository portfolioService;
    @KafkaListener(topics = "buy-order-placed", groupId = "portfolio-service")
    public void handleBuyOrderRequest(BuyOrderPlacedEvent event) {
        Optional<PortfolioItem> item = this.portfolioService.findByUserId(event.getUserId());
        System.out.println("PortFolioCheckerService.handleOrderRequest item: " + item.orElseGet(() -> PortfolioItem.builder().build()));
        System.out.println("PortFolioCheckerService.handleOrderRequest request: " + event);
    }

   // @KafkaListener(topics = "sell-order-placed", groupId = "portfolio-service")
    public void handleSellOrderRequest() {

        System.out.println("PortFolioCheckerService.handleOrderRequest");
    }
}


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BuyOrderPlacedEvent{
    private String stockCode;
    private Integer userId;
    private UUID uuid;
}