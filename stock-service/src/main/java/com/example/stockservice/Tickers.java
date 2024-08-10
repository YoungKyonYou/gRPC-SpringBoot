package com.example.stockservice;


import com.example.stockservice.service.event.StockPriceEvent;
import com.youyk.common.Ticker;
import com.youyk.stock.PriceUpdate;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Tickers {
    private final ApplicationEventPublisher publisher;

    private final Map<Ticker, Integer> map;

    public Tickers(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.map = new HashMap<>();
    }

    @PostConstruct
    private void init() {
        this.map.put(Ticker.APPLE, 100);
        this.map.put(Ticker.GOOGLE, 100);
        this.map.put(Ticker.AMAZON, 100);
        this.map.put(Ticker.MICROSOFT, 100);
    }

    public Optional<Integer> getPrice(Ticker stockSymbol) {
        return Optional.ofNullable(this.map.get(stockSymbol));
    }

    @Scheduled(fixedRate = 1000L)
    public void updatePrice() {
        this.map.keySet()
                .forEach(k -> {
                    this.map.computeIfPresent(k, (key, oldValue) -> oldValue + randomValue());
                    PriceUpdate stock = PriceUpdate.newBuilder().setTicker(k).setPrice((Integer) this.map.get(k)).build();
                    //StockServiceImpl의 onApplicationEvent 메서드를 호출합니다.
                    this.publisher.publishEvent((ApplicationEvent)new StockPriceEvent(this, stock));
                });
    }

    private int randomValue() {
        return ThreadLocalRandom.current().nextInt(-3, 4);
    }
}

