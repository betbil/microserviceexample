package com.example.stockservice.cmd;

import com.example.stockservice.config.ApplicationProperties;
import com.example.stockservice.model.Stock;
import com.example.stockservice.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DataCreator implements CommandLineRunner {
    private final StockRepository stockRepository;
    private final ApplicationProperties applicationProperties;
    private final static String APPLE = "APPL";
    private final static Double APPLE_PRICE = 100.0;

    @Override
    public void run(String... args) throws Exception {
        if(applicationProperties.isUpdatedb()){
            List<Stock> stockList = new ArrayList<>();
            for (int i = 0; i < 10; i++){
                Stock stock = Stock.builder()
                        .productCode(DataCreator.APPLE)
                        .price(DataCreator.APPLE_PRICE)
                        .build();
                stockList.add(stock);
            }
            this.stockRepository.saveAll(stockList);
        }
    }
}