package com.example.userservice.controller;

import com.example.userservice.exceptions.InvalidRequestException;
import com.example.userservice.model.APPUser;
import com.example.userservice.model.BuyOrderRequest;
import com.example.userservice.model.SellOrderRequest;
import com.example.userservice.service.UserOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserOrderController {

    private final UserOrderService userService;

    private Integer getUserIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof APPUser) {
                Integer userId = ((APPUser) principal).getId();
                log.debug("User ID: {}", userId);
                return userId;
            }
        }
        return 0;
    }
   @PostMapping("/buy-order")
   @ResponseStatus(HttpStatus.ACCEPTED)
    public void buyOrder(@RequestBody @Valid BuyOrderRequest buyOrderRequest) {
        if (buyOrderRequest.getUserId() == null){
            buyOrderRequest.setUserId(getUserIdFromAuth());
        }
       log.info("buyOrder request received: {}", buyOrderRequest);
       this.userService.checkStockExists(buyOrderRequest.getStockCode());
       this.userService.buyOrder(buyOrderRequest);

    }

    @PostMapping("/sell-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sellOrder(@RequestBody @Valid SellOrderRequest sellOrderRequest) {
        if (sellOrderRequest.getUserId() == null){
            sellOrderRequest.setUserId(getUserIdFromAuth());
        }

        log.info("sellOrder request received: {}", sellOrderRequest);
        this.userService.sellOrder(sellOrderRequest);

    }

    @PostMapping("/cancel-order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancelOrder(@RequestParam(name = "orderId", required = true) String orderID) {
        Integer userID = getUserIdFromAuth();
        if ((userID == null) || (userID == 0)){
            throw new InvalidRequestException("Invalid Request Exception");
        }

        log.info("cancelOrder request received: orderID {}, userID {}", orderID, userID);
        this.userService.cancelOrder(orderID,userID);
    }

    @GetMapping("")
    public ResponseEntity<APPUser>  GetUser() {
        //get user from auth
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof APPUser) {
                return ResponseEntity.ok((APPUser) principal);
            }
        }

        return ResponseEntity.notFound().build();
    }
}
