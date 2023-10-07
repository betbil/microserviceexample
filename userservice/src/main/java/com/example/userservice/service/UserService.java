package com.example.userservice.service;

import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.model.UserOrderRequest;
import com.example.userservice.repository.UserOrderRequestRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UserService {

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
        this.kafkaTemplate.send("buy-order-placed", userOrderRequest.getId().toString(), BuyOrderPlaced.builder()
                .stockCode(buyOrderRequest.getStockCode())
                .userId(buyOrderRequest.getUserId())
                .uuid(userOrderRequest.getId())
                .build());
    }
}

//kafka event
@Data
@Builder
class BuyOrderPlaced{
    private String stockCode;
    private Integer userId;
    private UUID uuid;
}