package com.example.stockservice.service;

import com.example.stockservice.model.StockStatus;
import com.example.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    public StockStatus getStockStatus(String stockCode) {
        StockStatus status = new StockStatus(false);
        status.setExists(this.stockRepository.existsByProductCode(stockCode));
        return status;
    }
}
