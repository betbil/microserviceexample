package com.example.userservice.service;

import com.example.userservice.model.StockStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "stock-service", url = "${app.stockservice.stockServiceUrl}")
public interface StockClient {
    @GetMapping("/stock-exists")
    StockStatus getStockStatus(@RequestParam("stockCode") String stockCode) throws Exception;
}

