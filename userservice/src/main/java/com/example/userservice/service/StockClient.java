package com.example.userservice.service;

import com.example.userservice.model.StockStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://localhost:8093", name = "stock-service") //TODO: change url from service and application.yml like a service discovery
public interface StockClient {
    @GetMapping("/stock-exists")
    StockStatus getStockStatus(@RequestParam("stockCode") String stockCode) throws Exception;
}


