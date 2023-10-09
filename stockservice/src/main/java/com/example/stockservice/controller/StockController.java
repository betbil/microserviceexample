package com.example.stockservice.controller;

import com.example.stockservice.model.StockStatus;
import com.example.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final StockService stockService;
    @GetMapping("/stock-exists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StockStatus stockExists(@RequestParam(name = "stockCode", required = true) String stockCode) {
        log.info("stockExists request received: stockId {}", stockCode);
        return stockService.getStockStatus(stockCode);
    }
}

