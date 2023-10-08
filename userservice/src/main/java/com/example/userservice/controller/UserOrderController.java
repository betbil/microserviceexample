package com.example.userservice.controller;

import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.model.SellOrderRequest;
import com.example.userservice.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserOrderController {

    private final UserOrderService userService;

   @PostMapping("/buy-order")
   @ResponseStatus(HttpStatus.ACCEPTED)
    public void buyOrder(@RequestBody BuyOrderRequest buyOrderRequest) {
       //TODO: userid jwt den alacak şekide değiştir
       System.out.println("Buy order request received: " + buyOrderRequest);
       this.userService.buyOrder(buyOrderRequest);

    }

    @PostMapping("/sell-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sellOrder(@RequestBody SellOrderRequest sellOrderRequest) {
        //TODO: userid jwt den alacak şekide değiştir
        System.out.println("Buy order request received: " + sellOrderRequest);
        this.userService.sellOrder(sellOrderRequest);

    }

    @PostMapping("/cancel-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancelOrder(@RequestParam(name = "orderId", required = true) String orderID,@RequestParam(name = "userID", required = true) Integer userId) {
        //TODO: userid jwt den alacak şekide değiştir
        this.userService.cancelOrder(orderID,userId);
    }
}
