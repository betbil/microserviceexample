package com.example.userservice.controller;

import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.model.SellOrderRequest;
import com.example.userservice.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserOrderController {

    private final UserOrderService userService;

   @PostMapping("/buy-order")
   @ResponseStatus(HttpStatus.ACCEPTED)
    public void buyOrder(@RequestBody BuyOrderRequest buyOrderRequest) {
       //TODO: userid jwt den alacak şekide değiştir
       log.info("buyOrder request received: {}", buyOrderRequest);
       //TODO: SONRA AC STARTS TODOBETUL
       //this.userService.checkStockExists(buyOrderRequest.getStockCode());
       //TODO: SONRA AC ENDS
       this.userService.buyOrder(buyOrderRequest);

    }

    @PostMapping("/sell-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sellOrder(@RequestBody SellOrderRequest sellOrderRequest) {
        //TODO: userid jwt den alacak şekide değiştir
        log.info("sellOrder request received: {}", sellOrderRequest);
        this.userService.sellOrder(sellOrderRequest);

    }

    @PostMapping("/cancel-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancelOrder(@RequestParam(name = "orderId", required = true) String orderID,@RequestParam(name = "userID", required = true) Integer userId) {
        //TODO: userid jwt den alacak şekide değiştir
        log.info("cancelOrder request received: orderID {}, userID {}", orderID, userId);
        this.userService.cancelOrder(orderID,userId);
    }
}
