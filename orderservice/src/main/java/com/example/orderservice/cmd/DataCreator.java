package com.example.orderservice.cmd;

import com.example.orderservice.model.Stock;
import com.example.orderservice.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DataCreator implements CommandLineRunner {
    private final StockRepository stockRepository;
    private final static String APPLE = "AppleStock";
    private final static String SELLSTATUS = "SELL";

    @Override
    public void run(String... args) throws Exception {
        List<Stock> stockList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            Stock stock = Stock.builder()
                    .productCode(DataCreator.APPLE)
                    .status(DataCreator.SELLSTATUS)
                    .build();
            stockList.add(stock);
        }
        this.stockRepository.saveAll(stockList);

    }
}
