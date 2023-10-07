package com.example.userservice.controller;

import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

   @PostMapping("/buy-order")
   @ResponseStatus(HttpStatus.ACCEPTED)
    public void buyOrder(@RequestBody BuyOrderRequest buyOrderRequest) {
       System.out.println("Buy order request received: " + buyOrderRequest);
       this.userService.buyOrder(buyOrderRequest);

    }
}
