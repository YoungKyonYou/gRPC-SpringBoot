package com.example.stockservice.service.event;

import com.youyk.stock.PriceUpdate;
import org.springframework.context.ApplicationEvent;

public class StockPriceEvent extends ApplicationEvent {
    private final PriceUpdate priceUpdate;

    public StockPriceEvent(Object source, PriceUpdate priceUpdate) {
        super(source);
        this.priceUpdate = priceUpdate;
    }

    public PriceUpdate getPriceUpdate() {
        return this.priceUpdate;
    }
}

