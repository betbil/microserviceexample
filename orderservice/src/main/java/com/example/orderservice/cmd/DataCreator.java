package com.example.orderservice.cmd;

import com.example.orderservice.model.OrderStatusType;
import com.example.orderservice.model.SellOrder;
import com.example.orderservice.repository.SellOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DataCreator implements CommandLineRunner {
    private final SellOrderRepository sellOrderRepository;
    private final static String APPLE = "APPL";
    private final static Integer APPLE_CMPNY_ID = 1000; //some magic number, may be set to a negative number

    @Override
    public void run(String... args) throws Exception {
        List<SellOrder> intialSellOrderListOfCmpny = new ArrayList<>();
        for (int i = 1; i <= 10; i++){
            SellOrder sellOrder = SellOrder.builder()
                    .stockId(i)
                    .userId(DataCreator.APPLE_CMPNY_ID)
                    .status(OrderStatusType.PENDING)
                    .build();

            if (sellOrder.getId() == null) {
                sellOrder.setId(UUID.randomUUID());
            }

            intialSellOrderListOfCmpny.add(sellOrder);
        }
        this.sellOrderRepository.saveAll(intialSellOrderListOfCmpny);
    }
}